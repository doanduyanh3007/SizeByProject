package com.finalproject2.controller;

import com.finalproject2.SecurityConfiguration.VNPayConfig;
import com.finalproject2.entity.Order;
import com.finalproject2.repository.OrderRepository;
import com.finalproject2.mapper.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(originPatterns = "*")
public class PaymentController {

    private final VNPayConfig vnPayConfig;
    private final OrderRepository orderRepo;

    public PaymentController(VNPayConfig vnPayConfig, OrderRepository orderRepo) {
        this.vnPayConfig = vnPayConfig;
        this.orderRepo = orderRepo;
    }

    @PostMapping("/vnpay/create")
    public ResponseEntity<?> createVnpayPayment(
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        try {
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID " + orderId));

            BigDecimal amount = order.getFinalAmount() != null
                    ? order.getFinalAmount()
                    : order.getTotalMoney();
            if (amount == null) {
                throw new RuntimeException("Đơn hàng chưa có số tiền thanh toán hợp lệ");
            }

            long vnpAmount = amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();

            String vnpIpAddr = VNPayUtil.getIpAddress(request);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            DateTimeFormatter vnpDateFmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String vnpCreateDate = now.format(vnpDateFmt);
            String vnpExpireDate = now.plusMinutes(15).format(vnpDateFmt);
            String vnpTxnRef = orderId + "_" + vnpCreateDate + ThreadLocalRandom.current().nextInt(100, 999);

            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
            vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", vnpTxnRef);
            vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
            vnpParams.put("vnp_IpAddr", vnpIpAddr);
            vnpParams.put("vnp_CreateDate", vnpCreateDate);
            vnpParams.put("vnp_ExpireDate", vnpExpireDate);

            String paymentUrl = VNPayUtil.buildPaymentUrl(
                    vnPayConfig.getVnpPayUrl(),
                    vnpParams,
                    vnPayConfig.getVnpHashSecret()
            );

            order.setPaymentStatus("PENDING");
            orderRepo.save(order);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "paymentUrl", paymentUrl,
                    "orderId", orderId
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi tạo thanh toán VNPay: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/vnpay/ipn")
    @Transactional
    public ResponseEntity<?> vnpayIpn(HttpServletRequest request) {
        try {
            Map<String, String> params = VNPayUtil.fromRequest(request);
            boolean isValidHash = VNPayUtil.verifyHash(
                    params, vnPayConfig.getVnpHashSecret(), request.getQueryString());
            if (!isValidHash) {
                return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid Checksum"));
            }

            String vnpTxnRef = params.get("vnp_TxnRef");
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTransactionStatus = params.get("vnp_TransactionStatus");
            String vnpAmountStr = params.get("vnp_Amount");

            Long orderId;
            try {
                orderId = VNPayUtil.parseOrderId(vnpTxnRef);
            } catch (Exception e) {
                return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Invalid order ID"));
            }

            if (orderId == null) {
                return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not found"));
            }

            Optional<Order> optOrder = orderRepo.findById(orderId);
            if (optOrder.isEmpty()) {
                return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not found"));
            }

            Order order = optOrder.get();

            if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                return ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Order already confirmed"));
            }

            if (vnpAmountStr != null) {
                long vnpAmount = Long.parseLong(vnpAmountStr);
                BigDecimal expectedAmount = order.getFinalAmount() != null
                        ? order.getFinalAmount() : order.getTotalMoney();
                long expectedVnpAmount = expectedAmount.movePointRight(2)
                        .setScale(0, RoundingMode.HALF_UP).longValueExact();

                if (vnpAmount != expectedVnpAmount) {
                    return ResponseEntity.ok(Map.of("RspCode", "04", "Message", "Invalid amount"));
                }
            }

            if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
                markOrderPaid(order);
                return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
            }

            order.setPaymentStatus("FAILED");
            orderRepo.save(order);
            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Unknown error: " + e.getMessage()));
        }
    }

    @GetMapping("/vnpay/return")
    @Transactional
    public ResponseEntity<?> vnpayReturnGet(HttpServletRequest request) {
        return handleVnpayReturn(VNPayUtil.fromRequest(request), request.getQueryString());
    }

    @PostMapping("/vnpay/return")
    @Transactional
    public ResponseEntity<?> vnpayReturnPost(@RequestBody Map<String, Object> payload) {
        Object raw = payload == null ? null : payload.get("rawQuery");
        String rawQuery = raw == null ? null : String.valueOf(raw);
        return handleVnpayReturn(VNPayUtil.sanitizeParams(payload), rawQuery);
    }

    private ResponseEntity<?> handleVnpayReturn(Map<String, String> params, String rawQuery) {
        try {
            boolean isValidHash = VNPayUtil.verifyHash(params, vnPayConfig.getVnpHashSecret(), rawQuery);
            String vnpTxnRef = params.get("vnp_TxnRef");
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTransactionNo = params.get("vnp_TransactionNo");
            String vnpAmount = params.get("vnp_Amount");
            boolean paymentOk = "00".equals(vnpResponseCode);

            Long orderId = null;
            try {
                orderId = VNPayUtil.parseOrderId(vnpTxnRef);
            } catch (NumberFormatException ignored) {
            }

            if (isValidHash && orderId != null) {
                Optional<Order> optOrder = orderRepo.findById(orderId);
                if (optOrder.isPresent()) {
                    Order order = optOrder.get();
                    if (!"PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                        if (paymentOk) {
                            markOrderPaid(order);
                        } else {
                            order.setPaymentStatus("FAILED");
                            orderRepo.save(order);
                        }
                    }
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("isValid", isValidHash);
            result.put("orderId", orderId != null ? String.valueOf(orderId) : vnpTxnRef);
            result.put("vnpResponseCode", vnpResponseCode);
            result.put("vnpTransactionNo", vnpTransactionNo);
            result.put("amount", vnpAmount != null ? Long.parseLong(vnpAmount) / 100 : 0);
            result.put("success", isValidHash && paymentOk);
            if (!isValidHash) {
                result.put("message", "Không xác minh được chữ ký VNPay. Giao dịch chưa được ghi nhận.");
            } else if (paymentOk) {
                result.put("message", "Thanh toán thành công");
            } else {
                result.put("message", "Thanh toán thất bại (Mã lỗi: " + vnpResponseCode + ")");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false, "message", "Lỗi xử lý kết quả VNPay: " + e.getMessage()));
        }
    }

    private void markOrderPaid(Order order) {
        order.setPaymentStatus("PAID");
        if (order.getStatus() == null
                || "PENDING".equalsIgnoreCase(order.getStatus())
                || "NEW".equalsIgnoreCase(order.getStatus())) {
            order.setStatus("PENDING");
        }
        orderRepo.save(order);
    }
}
