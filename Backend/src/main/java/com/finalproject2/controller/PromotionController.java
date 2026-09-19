package com.finalproject2.controller;

import com.finalproject2.entity.Product;
import com.finalproject2.entity.Promotion;
import com.finalproject2.repository.ProductRepository;
import com.finalproject2.repository.PromotionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(originPatterns = "*")
public class PromotionController {

    private final PromotionRepository promotionRepo;
    private final ProductRepository productRepo;

    public PromotionController(PromotionRepository promotionRepo, ProductRepository productRepo) {
        this.promotionRepo = promotionRepo;
        this.productRepo = productRepo;
    }

    @GetMapping
    public ResponseEntity<?> getAllPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        try {
            Page<Promotion> promotions = promotionRepo.findAll(
                    PageRequest.of(page, size, Sort.by(direction, sortBy))
            );

            List<Map<String, Object>> promotionList = new ArrayList<>();
            for (Promotion promotion : promotions) {
                promotionList.add(mapPromotionToResponse(promotion));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", promotionList);
            response.put("currentPage", page);
            response.put("totalPages", promotions.getTotalPages());
            response.put("totalElements", promotions.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{promotionId}")
    public ResponseEntity<?> getPromotionById(@PathVariable Long promotionId) {
        try {
            Promotion promotion = promotionRepo.findById(promotionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mại ID " + promotionId));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", mapPromotionToResponse(promotion)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createPromotion(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            Object discountPercentObj = payload.get("discountPercent");
            String startDateStr = (String) payload.get("startDate");
            String endDateStr = (String) payload.get("endDate");

            if (name == null || name.isBlank() || discountPercentObj == null
                    || startDateStr == null || endDateStr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Vui lòng cung cấp đầy đủ: name, discountPercent, startDate, endDate"
                ));
            }

            BigDecimal discountPercent = new BigDecimal(discountPercentObj.toString());
            if (discountPercent.compareTo(BigDecimal.ZERO) <= 0
                    || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Phần trăm giảm phải từ 1 đến 100"
                ));
            }

            Instant startDate = Instant.parse(startDateStr);
            Instant endDate = Instant.parse(endDateStr);
            if (!endDate.isAfter(startDate)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thời gian kết thúc phải sau thời gian bắt đầu"
                ));
            }

            Promotion promotion = new Promotion();
            promotion.setName(name.trim());
            promotion.setDiscountPercent(discountPercent);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            promotion.setIsActive((Boolean) payload.getOrDefault("isActive", true));
            promotion.setCreatedAt(Instant.now());
            promotion.setProducts(resolveProducts(payload));

            Promotion saved = promotionRepo.save(promotion);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tạo khuyến mại thành công!",
                    "data", mapPromotionToResponse(saved)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Định dạng dữ liệu không hợp lệ: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{promotionId}")
    @Transactional
    public ResponseEntity<?> updatePromotion(
            @PathVariable Long promotionId,
            @RequestBody Map<String, Object> payload) {
        try {
            Promotion promotion = promotionRepo.findById(promotionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mại ID " + promotionId));

            if (payload.containsKey("name")) {
                String name = (String) payload.get("name");
                if (name == null || name.isBlank()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Tên khuyến mại không được để trống"
                    ));
                }
                promotion.setName(name.trim());
            }

            if (payload.containsKey("discountPercent") && payload.get("discountPercent") != null) {
                BigDecimal discountPercent = new BigDecimal(payload.get("discountPercent").toString());
                if (discountPercent.compareTo(BigDecimal.ZERO) <= 0
                        || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Phần trăm giảm phải từ 1 đến 100"
                    ));
                }
                promotion.setDiscountPercent(discountPercent);
            }

            if (payload.containsKey("startDate") && payload.get("startDate") != null) {
                promotion.setStartDate(Instant.parse((String) payload.get("startDate")));
            }

            if (payload.containsKey("endDate") && payload.get("endDate") != null) {
                promotion.setEndDate(Instant.parse((String) payload.get("endDate")));
            }

            if (promotion.getStartDate() != null && promotion.getEndDate() != null
                    && !promotion.getEndDate().isAfter(promotion.getStartDate())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thời gian kết thúc phải sau thời gian bắt đầu"
                ));
            }

            if (payload.containsKey("isActive")) {
                promotion.setIsActive((Boolean) payload.get("isActive"));
            }

            if (payload.containsKey("productIds")) {
                promotion.setProducts(resolveProducts(payload));
            }

            promotion.setUpdatedAt(Instant.now());
            Promotion saved = promotionRepo.save(promotion);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật khuyến mại thành công!",
                    "data", mapPromotionToResponse(saved)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Định dạng dữ liệu không hợp lệ: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{promotionId}")
    @Transactional
    public ResponseEntity<?> deletePromotion(@PathVariable Long promotionId) {
        try {
            if (!promotionRepo.existsById(promotionId)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Không tìm thấy khuyến mại ID " + promotionId
                ));
            }

            promotionRepo.deleteById(promotionId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa khuyến mại thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    private Set<Product> resolveProducts(Map<String, Object> payload) {
        Object productIdsObj = payload.get("productIds");
        if (!(productIdsObj instanceof List<?> productIds) || productIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<Product> products = new LinkedHashSet<>();
        for (Object rawId : productIds) {
            Long productId = Long.parseLong(rawId.toString());
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID " + productId));
            products.add(product);
        }
        return products;
    }

    private Map<String, Object> mapPromotionToResponse(Promotion promotion) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", promotion.getId());
        response.put("name", promotion.getName());
        response.put("discountPercent", promotion.getDiscountPercent());
        response.put("startDate", promotion.getStartDate());
        response.put("endDate", promotion.getEndDate());
        response.put("isActive", promotion.getIsActive());
        response.put("createdAt", promotion.getCreatedAt());
        response.put("updatedAt", promotion.getUpdatedAt());

        List<Map<String, Object>> productList = promotion.getProducts().stream()
                .map(product -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", product.getId());
                    item.put("name", product.getName());
                    item.put("productCode", product.getProductCode());
                    return item;
                })
                .collect(Collectors.toList());

        response.put("products", productList);
        response.put("productIds", productList.stream().map(item -> item.get("id")).collect(Collectors.toList()));
        response.put("status", resolvePromotionStatus(promotion));
        return response;
    }

    private String resolvePromotionStatus(Promotion promotion) {
        if (Boolean.FALSE.equals(promotion.getIsActive())) {
            return "INACTIVE";
        }

        Instant now = Instant.now();
        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
            return "SCHEDULED";
        }
        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
            return "EXPIRED";
        }
        return "ACTIVE";
    }
}
