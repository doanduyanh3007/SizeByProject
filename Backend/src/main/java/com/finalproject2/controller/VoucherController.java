package com.finalproject2.controller;

import com.finalproject2.entity.Order;
import com.finalproject2.entity.Voucher;
import com.finalproject2.repository.OrderRepository;
import com.finalproject2.repository.VoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(originPatterns = "*")
public class VoucherController {

    private final VoucherRepository voucherRepo;
    private final OrderRepository orderRepo;

    public VoucherController(VoucherRepository voucherRepo, OrderRepository orderRepo) {
        this.voucherRepo = voucherRepo;
        this.orderRepo = orderRepo;
    }

    @GetMapping
    public ResponseEntity<?> getAllVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        try {
            Page<Voucher> vouchers = voucherRepo.findAll(
                    PageRequest.of(page, size, Sort.by(direction, sortBy))
            );

            List<Map<String, Object>> voucherList = new ArrayList<>();
            for (Voucher voucher : vouchers) {
                voucherList.add(mapVoucherToResponse(voucher));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", voucherList);
            response.put("currentPage", page);
            response.put("totalPages", vouchers.getTotalPages());
            response.put("totalElements", vouchers.getTotalElements());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    /**
     * GET VOUCHER BY ID
     * GET /api/vouchers/{voucherId}
     */
    @GetMapping("/{voucherId}")
    public ResponseEntity<?> getVoucherById(@PathVariable Long voucherId) {
        try {
            Voucher voucher = voucherRepo.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá ID " + voucherId));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", mapVoucherToResponse(voucher)
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    /**
     * CREATE NEW VOUCHER
     * POST /api/vouchers
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> createVoucher(@RequestBody Map<String, Object> payload) {
        try {
            String code = (String) payload.get("code");
            String discountType = (String) payload.get("discountType");
            Object discountValueObj = payload.get("discountValue");
            Object minOrderValueObj = payload.get("minOrderValue");
            String startDateStr = (String) payload.get("startDate");
            String endDateStr = (String) payload.get("endDate");
            Boolean isActive = (Boolean) payload.getOrDefault("isActive", true);

            if (code == null || code.isEmpty() ||
                    discountType == null || discountValueObj == null ||
                    minOrderValueObj == null || startDateStr == null || endDateStr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Vui lòng cung cấp đầy đủ: code, discountType, discountValue, minOrderValue, startDate, endDate"
                ));
            }

            if (!discountType.equals("PERCENT") && !discountType.equals("AMOUNT")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "discountType phải là PERCENT hoặc AMOUNT"
                ));
            }

            if ("PERCENT".equals(discountType)) {
                java.math.BigDecimal val = new java.math.BigDecimal(discountValueObj.toString());
                if (val.compareTo(new java.math.BigDecimal("50")) > 0) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Mức giảm giá theo phần trăm không được vượt quá 50%"
                    ));
                }
            }

            if (voucherRepo.findByCode(code).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Mã giảm giá '" + code + "' đã tồn tại!"
                ));
            }

            Voucher voucher = new Voucher();
            voucher.setCode(code);
            voucher.setDiscountType(discountType);
            voucher.setDiscountValue(new java.math.BigDecimal(discountValueObj.toString()));
            voucher.setMinOrderValue(new java.math.BigDecimal(minOrderValueObj.toString()));
            voucher.setStartDate(Instant.parse(startDateStr));
            voucher.setEndDate(Instant.parse(endDateStr));
            voucher.setIsActive(isActive);
            if (payload.containsKey("maxDiscountAmount") && payload.get("maxDiscountAmount") != null) {
                voucher.setMaxDiscountAmount(new java.math.BigDecimal(payload.get("maxDiscountAmount").toString()));
            }
            voucher.setUsedCount(0);

            // Optional usageLimit from payload
            if (payload.containsKey("usageLimit") && payload.get("usageLimit") != null) {
                voucher.setUsageLimit(Integer.parseInt(payload.get("usageLimit").toString()));
            }

            voucher.setCreatedAt(Instant.now());

            Voucher saved = voucherRepo.save(voucher);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tạo mã giảm giá thành công!",
                    "data", mapVoucherToResponse(saved)
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Định dạng ngày giờ không hợp lệ: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    /**
     * UPDATE VOUCHER
     * PUT /api/vouchers/{voucherId}
     */
    @PutMapping("/{voucherId}")
    @Transactional
    public ResponseEntity<?> updateVoucher(@PathVariable Long voucherId, @RequestBody Map<String, Object> payload) {
        try {
            Voucher voucher = voucherRepo.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá ID " + voucherId));

            if (payload.containsKey("code")) {
                String newCode = (String) payload.get("code");
                if (!newCode.equals(voucher.getCode())) {
                    if (voucherRepo.findByCode(newCode).isPresent()) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "success", false,
                                "message", "Mã giảm giá '" + newCode + "' đã tồn tại!"
                        ));
                    }
                }
                voucher.setCode(newCode);
            }

            if (payload.containsKey("discountType")) {
                String type = (String) payload.get("discountType");
                if (!type.equals("PERCENT") && !type.equals("AMOUNT")) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "discountType phải là PERCENT hoặc AMOUNT"
                    ));
                }
                voucher.setDiscountType(type);
            }

            if (payload.containsKey("discountValue")) {
                voucher.setDiscountValue(new java.math.BigDecimal(payload.get("discountValue").toString()));
            }

            if ("PERCENT".equals(voucher.getDiscountType())) {
                if (voucher.getDiscountValue() != null && voucher.getDiscountValue().compareTo(new java.math.BigDecimal("50")) > 0) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Mức giảm giá theo phần trăm không được vượt quá 50%"
                    ));
                }
            }

            if (payload.containsKey("minOrderValue")) {
                voucher.setMinOrderValue(new java.math.BigDecimal(payload.get("minOrderValue").toString()));
            }

            if (payload.containsKey("startDate")) {
                voucher.setStartDate(Instant.parse((String) payload.get("startDate")));
            }

            if (payload.containsKey("endDate")) {
                voucher.setEndDate(Instant.parse((String) payload.get("endDate")));
            }

            if (payload.containsKey("isActive")) {
                voucher.setIsActive((Boolean) payload.get("isActive"));
            }

            if (payload.containsKey("usageLimit")) {
                Object limitObj = payload.get("usageLimit");
                voucher.setUsageLimit(limitObj != null ? Integer.parseInt(limitObj.toString()) : null);
            }

            if (payload.containsKey("maxDiscountAmount")) {
                Object maxObj = payload.get("maxDiscountAmount");
                voucher.setMaxDiscountAmount(maxObj != null ? new java.math.BigDecimal(maxObj.toString()) : null);
            }

            voucher.setUpdatedAt(Instant.now());
            Voucher updated = voucherRepo.save(voucher);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật mã giảm giá thành công!",
                    "data", mapVoucherToResponse(updated)
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    /**
     * DELETE VOUCHER
     * DELETE /api/vouchers/{voucherId}
     */
    @DeleteMapping("/{voucherId}")
    @Transactional
    public ResponseEntity<?> deleteVoucher(@PathVariable Long voucherId) {
        try {
            Voucher voucher = voucherRepo.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá ID " + voucherId));

            // Null out voucher reference in all orders that used this voucher
            // before deleting to avoid FK constraint violation.
            List<Order> ordersUsingVoucher = orderRepo.findAll().stream()
                    .filter(o -> o.getVoucher() != null && voucherId.equals(o.getVoucher().getId()))
                    .toList();
            for (Order order : ordersUsingVoucher) {
                order.setVoucher(null);
                orderRepo.save(order);
            }

            voucherRepo.delete(voucher);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa mã giảm giá thành công!"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    /**
     * APPLY VOUCHER PREVIEW (read-only — validates and returns discount, does NOT increment usedCount)
     * GET /api/vouchers/apply?code=SUMMER20&orderTotal=500000
     *
     * Call this to show the user how much they save before placing the order.
     * After the order is successfully placed, call POST /api/vouchers/use?code=... to record usage.
     */
    @GetMapping("/apply")
    public ResponseEntity<?> previewVoucher(
            @RequestParam String code,
            @RequestParam(required = false) java.math.BigDecimal orderTotal,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer accountId) {
        try {
            Voucher voucher = voucherRepo.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá: " + code));

            ResponseEntity<?> validationError = validateVoucherForUse(voucher, orderTotal, phone, accountId);
            if (validationError != null) return validationError;

            java.math.BigDecimal discountAmount = calculateDiscount(voucher, orderTotal);

            return ResponseEntity.ok(buildApplyResponse(voucher, discountAmount));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateVoucher(
            @RequestParam String code,
            @RequestParam(required = false) java.math.BigDecimal orderTotal,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer accountId) {
        try {
            Voucher voucher = voucherRepo.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá: " + code));

            ResponseEntity<?> validationError = validateVoucherForUse(voucher, orderTotal, phone, accountId);
            if (validationError != null) return validationError;

            java.math.BigDecimal discountAmount = calculateDiscount(voucher, orderTotal);
            return ResponseEntity.ok(buildApplyResponse(voucher, discountAmount));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * USE VOUCHER — increments usedCount by 1 (call after order is successfully placed)
     * POST /api/vouchers/use?code=SUMMER20&orderTotal=500000
     *
     * Returns the same shape as GET /apply so the frontend can update its state.
     */
    @PostMapping("/use")
    @Transactional
    public ResponseEntity<?> useVoucher(
            @RequestParam String code,
            @RequestParam(required = false) java.math.BigDecimal orderTotal,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer accountId) {
        try {
            Voucher voucher = voucherRepo.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá: " + code));

            ResponseEntity<?> validationError = validateVoucherForUse(voucher, orderTotal, phone, accountId);
            if (validationError != null) return validationError;

            java.math.BigDecimal discountAmount = calculateDiscount(voucher, orderTotal);

            // Increment usage
            int newCount = (voucher.getUsedCount() != null ? voucher.getUsedCount() : 0) + 1;
            voucher.setUsedCount(newCount);
            voucher.setUpdatedAt(Instant.now());
            voucherRepo.save(voucher);

            return ResponseEntity.ok(buildApplyResponse(voucher, discountAmount));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * GET ACTIVE VOUCHERS
     * GET /api/vouchers/status/active
     */
    @GetMapping("/status/active")
    public ResponseEntity<?> getActiveVouchers(
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) String phone) {
        try {
            List<Voucher> vouchers = voucherRepo.findAll().stream()
                    .filter(v -> "ACTIVE".equals(determineVoucherStatus(v)))
                    .toList();

            List<Map<String, Object>> voucherList = new ArrayList<>();
            for (Voucher voucher : vouchers) {
                Map<String, Object> vMap = mapVoucherToResponse(voucher);
                boolean usedByAccount = false;

                if (accountId != null) {
                    int usage = orderRepo.countByAccountIdAndVoucherId(accountId, voucher.getId());
                    if (usage > 0) usedByAccount = true;
                }
                if (!usedByAccount && phone != null && !phone.trim().isEmpty()) {
                    int usage = orderRepo.countByPhoneAndVoucherId(phone.trim(), voucher.getId());
                    if (usage > 0) usedByAccount = true;
                }

                vMap.put("usedByCurrentUser", usedByAccount);
                voucherList.add(vMap);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", voucherList
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    /**
     * SEND VOUCHER TO ACCOUNTS
     * POST /api/vouchers/{voucherId}/send
     */
    @PostMapping("/{voucherId}/send")
    public ResponseEntity<?> sendVoucherToAccounts(
            @PathVariable Long voucherId,
            @RequestBody Map<String, List<Integer>> payload) {
        try {
            Voucher voucher = voucherRepo.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá ID " + voucherId));

            List<Integer> accountIds = payload.get("accountIds");
            if (accountIds == null || accountIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Danh sách khách hàng trống!"
                ));
            }

            // MOCK: Sending logic here (e.g. Email, SMS, or saving to DB)
            // Currently just returns success message
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã gửi mã giảm giá thành công cho " + accountIds.size() + " khách hàng!"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    // ─── Private helpers ───────────────────────────────────────────────────────

    /**
     * Validates active status, date range, usage limit, and minimum order value.
     * Returns a 400 ResponseEntity when invalid, or null when valid.
     */
    private ResponseEntity<?> validateVoucherForUse(Voucher voucher, java.math.BigDecimal orderTotal, String phone, Integer accountId) {
        if (!voucher.getIsActive()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Mã giảm giá không còn hoạt động!"
            ));
        }

        Instant now = Instant.now();
        if (now.isBefore(voucher.getStartDate())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Mã giảm giá chưa bắt đầu hoạt động!"
            ));
        }

        if (now.isAfter(voucher.getEndDate())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Mã giảm giá đã hết hạn!"
            ));
        }

        int usedCount = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;
        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() > 0
                && usedCount >= voucher.getUsageLimit()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Mã giảm giá đã đạt giới hạn sử dụng!"
            ));
        }

        if (orderTotal != null && voucher.getMinOrderValue() != null
                && orderTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Đơn hàng chưa đạt giá trị tối thiểu "
                            + String.format("%,.0f", voucher.getMinOrderValue()) + "đ để dùng mã này!"
            ));
        }

        if (accountId != null) {
            int accountUsage = orderRepo.countByAccountIdAndVoucherId(accountId, voucher.getId());
            if (accountUsage > 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Tài khoản của bạn đã sử dụng mã giảm giá này!"
                ));
            }
        }
        
        if (phone != null && !phone.trim().isEmpty()) {
            int phoneUsage = orderRepo.countByPhoneAndVoucherId(phone.trim(), voucher.getId());
            if (phoneUsage > 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Số điện thoại " + phone + " đã sử dụng mã giảm giá này!"
                ));
            }
        }

        return null; // valid
    }

    private java.math.BigDecimal calculateDiscount(Voucher voucher, java.math.BigDecimal orderTotal) {
        java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
        if (orderTotal != null && orderTotal.compareTo(java.math.BigDecimal.ZERO) > 0) {
            if ("AMOUNT".equals(voucher.getDiscountType())) {
                discountAmount = voucher.getDiscountValue();
                if (discountAmount.compareTo(orderTotal) > 0) discountAmount = orderTotal;
            } else if ("PERCENT".equals(voucher.getDiscountType())) {
                discountAmount = orderTotal.multiply(voucher.getDiscountValue())
                        .divide(new java.math.BigDecimal("100"), java.math.BigDecimal.ROUND_DOWN);
                if (voucher.getMaxDiscountAmount() != null
                        && discountAmount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                    discountAmount = voucher.getMaxDiscountAmount();
                }
            }
        }
        return discountAmount;
    }

    private Map<String, Object> buildApplyResponse(Voucher voucher, java.math.BigDecimal discountAmount) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("code", voucher.getCode());
        response.put("discountType", voucher.getDiscountType());
        response.put("discountValue", voucher.getDiscountValue());
        response.put("discountAmount", discountAmount);
        response.put("minOrderValue", voucher.getMinOrderValue());
        response.put("maxDiscountAmount", voucher.getMaxDiscountAmount());
        response.put("usedCount", voucher.getUsedCount() != null ? voucher.getUsedCount() : 0);
        response.put("usageLimit", voucher.getUsageLimit() != null ? voucher.getUsageLimit() : 0);
        response.put("status", determineVoucherStatus(voucher));
        response.put("statusLabel", getVoucherStatusLabel(determineVoucherStatus(voucher)));
        return response;
    }

    /**
     * HELPER METHOD - Map Voucher entity to response DTO (now includes usageLimit and usedCount).
     */
    private Map<String, Object> mapVoucherToResponse(Voucher voucher) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", voucher.getId());
        response.put("code", voucher.getCode());
        response.put("discountType", voucher.getDiscountType());
        response.put("discountValue", voucher.getDiscountValue());
        response.put("minOrderValue", voucher.getMinOrderValue());
        response.put("startDate", voucher.getStartDate());
        response.put("endDate", voucher.getEndDate());
        response.put("isActive", voucher.getIsActive());
        response.put("usageLimit", voucher.getUsageLimit());
        response.put("usedCount", voucher.getUsedCount() != null ? voucher.getUsedCount() : 0);
        response.put("maxDiscountAmount", voucher.getMaxDiscountAmount());
        response.put("status", determineVoucherStatus(voucher));
        response.put("statusLabel", getVoucherStatusLabel(determineVoucherStatus(voucher)));
        response.put("createdAt", voucher.getCreatedAt());
        if (voucher.getUpdatedAt() != null) {
            response.put("updatedAt", voucher.getUpdatedAt());
        }
        return response;
    }

    private String determineVoucherStatus(Voucher voucher) {
        if (voucher == null) {
            return "INACTIVE";
        }
        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            return "INACTIVE";
        }

        Instant now = Instant.now();
        if (now.isBefore(voucher.getStartDate())) {
            return "SCHEDULED";
        }
        if (now.isAfter(voucher.getEndDate())) {
            return "EXPIRED";
        }

        int usedCount = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;
        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() > 0 && usedCount >= voucher.getUsageLimit()) {
            return "USED";
        }

        return "ACTIVE";
    }

    private String getVoucherStatusLabel(String status) {
        return switch (status) {
            case "ACTIVE" -> "Đang hoạt động";
            case "SCHEDULED" -> "Sắp diễn ra";
            case "EXPIRED" -> "Hết hạn";
            case "USED" -> "Đã hết lượt";
            default -> "Không hoạt động";
        };
    }
}

