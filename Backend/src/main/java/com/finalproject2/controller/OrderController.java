package com.finalproject2.controller;

import com.finalproject2.entity.*;
import com.finalproject2.repository.*;
import com.finalproject2.exception.BadRequestException;
import com.finalproject2.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(originPatterns = "*")
public class OrderController {

    private static final BigDecimal STANDARD_SHIPPING_FEE = new BigDecimal("30000");

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final PaymentMethodRepository paymentRepo;
    private final VoucherRepository voucherRepo;
    private final PromotionRepository promotionRepo;
    private final ProductVariantRepository variantRepo;
    private final AccountRepository accountRepo;
    private final AccountVoucherRepository accountVoucherRepo;
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;

    public OrderController(OrderRepository orderRepo,
            OrderItemRepository orderItemRepo,
            PaymentMethodRepository paymentRepo,
            VoucherRepository voucherRepo,
            PromotionRepository promotionRepo,
            ProductVariantRepository variantRepo,
            AccountRepository accountRepo,
            AccountVoucherRepository accountVoucherRepo,
            CartRepository cartRepo,
            CartItemRepository cartItemRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.paymentRepo = paymentRepo;
        this.voucherRepo = voucherRepo;
        this.promotionRepo = promotionRepo;
        this.variantRepo = variantRepo;
        this.accountRepo = accountRepo;
        this.accountVoucherRepo = accountVoucherRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> payload) {
        Integer accountId = asInteger(payload.get("accountId"));
        if (accountId == null) {
            throw new BadRequestException("Tài khoản không tồn tại");
        }
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Tài khoản không tồn tại"));

        Long pmId = asLong(payload.get("paymentMethodId"));
        if (pmId == null) {
            throw new BadRequestException("Phương thức thanh toán không hợp lệ");
        }
        PaymentMethod pm = paymentRepo.findById(pmId)
                .orElseThrow(() -> new BadRequestException("Phương thức thanh toán không hợp lệ"));

        String voucherCode = asString(payload.get("voucherCode"));
        Long voucherId = asLong(payload.get("voucherId"));
        Voucher voucher = null;
        if (voucherCode != null && !voucherCode.isBlank()) {
            voucher = voucherRepo.findByCode(voucherCode.trim())
                    .orElseThrow(() -> new BadRequestException("Mã giảm giá không tồn tại"));
        } else if (voucherId != null) {
            voucher = voucherRepo.findById(voucherId)
                    .orElseThrow(() -> new BadRequestException("Mã giảm giá không tồn tại"));
        }

        List<Map<String, Object>> itemsReq = extractOrderItems(payload);
        if (itemsReq == null || itemsReq.isEmpty()) {
            System.out.println("=== LỖI PAYLOAD KHÔNG TÌM THẤY GIỎ HÀNG ===");
            System.out.println("Dữ liệu Frontend gửi xuống: " + payload);
            throw new BadRequestException("Giỏ hàng đang trống, không thể đặt hàng!");
        }

        Order order = new Order();
        order.setAccount(account);
        order.setCreatedAt(Instant.now());
        order.setStatus("PENDING");
        order.setFullname(asString(payload.get("fullname")));
        order.setPhone(asString(payload.get("phone")));
        order.setShippingAddress(asString(payload.get("shippingAddress")));
        order.setEmail(asString(firstNonBlank(payload.get("email"), payload.get("gmail"))));
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod(pm);
        if (voucher != null) {
            order.setVoucher(voucher);
        }

        BigDecimal totalMoney = BigDecimal.ZERO;
        BigDecimal promotionDiscount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> itemReq : itemsReq) {
            Long variantId = extractVariantId(itemReq);
            Integer qty = asInteger(itemReq.get("quantity"));
            if (variantId == null) {
                throw new BadRequestException("Sản phẩm không hợp lệ");
            }
            if (qty == null || qty <= 0) {
                throw new BadRequestException("Số lượng sản phẩm không hợp lệ");
            }

            ProductVariant variant = variantRepo.findById(variantId)
                    .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));

            if (!isVariantSellable(variant)) {
                throw new BadRequestException(
                        "Sáº£n pháº©m " + variant.getProduct().getName() + " hiá»‡n khÃ´ng má»Ÿ bÃ¡n!");
            }

            int updatedRows = variantRepo.decrementStockIfAvailable(variantId, qty);
            if (updatedRows == 0) {
                throw new BadRequestException(
                        "Sản phẩm " + variant.getProduct().getName() + " không đủ số lượng trong kho!");
            }

            BigDecimal price = variant.getPrice();
            totalMoney = totalMoney.add(price.multiply(BigDecimal.valueOf(qty)));
            BigDecimal promotionRate = getPromotionRate(variant.getProduct().getId());
            BigDecimal salePrice = price;
            if (promotionRate.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal itemPromotionDiscount = price
                        .multiply(BigDecimal.valueOf(qty))
                        .multiply(promotionRate)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                promotionDiscount = promotionDiscount.add(itemPromotionDiscount);
                salePrice = price.multiply(BigDecimal.ONE.subtract(
                        promotionRate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP)));
            }

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setVariant(variant);
            oi.setQuantity(qty);
            oi.setPrice(salePrice.setScale(2, RoundingMode.HALF_UP));
            orderItems.add(oi);
        }

        order.setTotalMoney(totalMoney);

        if (voucher != null) {
            validateVoucherForOrder(voucher, totalMoney.subtract(promotionDiscount));
        }

        BigDecimal voucherDiscount = calculateDiscount(voucher, totalMoney.subtract(promotionDiscount));
        BigDecimal discount = promotionDiscount.add(voucherDiscount);
        BigDecimal shippingFee = STANDARD_SHIPPING_FEE;
        order.setDiscountAmount(discount);
        order.setFinalAmount(totalMoney.subtract(promotionDiscount).subtract(voucherDiscount).add(shippingFee));

        Order savedOrder = orderRepo.save(order);
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepo.saveAll(orderItems);

        if (savedOrder.getVoucher() != null) {
            Voucher v = savedOrder.getVoucher();
            int currentUsed = (v.getUsedCount() != null) ? v.getUsedCount() : 0;
            v.setUsedCount(currentUsed + 1);
            voucherRepo.save(v);

            AccountVoucher av = new AccountVoucher();
            av.setAccount(account);
            av.setVoucher(v);
            av.setUsedAt(Instant.now());
            accountVoucherRepo.save(av);
        }

        cartRepo.findByAccountId(accountId).ifPresent(cart -> cartItemRepo.deleteByCartId(cart.getId()));

        return ResponseEntity.ok(Map.of("success", true, "orderId", savedOrder.getId()));
    }

    private BigDecimal getPromotionRate(Long productId) {
        Instant now = Instant.now();
        return promotionRepo.findAll().stream()
                .filter(promotion -> Boolean.TRUE.equals(promotion.getIsActive()))
                .filter(promotion -> promotion.getStartDate() != null && promotion.getEndDate() != null)
                .filter(promotion -> !now.isBefore(promotion.getStartDate()) && !now.isAfter(promotion.getEndDate()))
                .filter(promotion -> promotion.getProducts().stream()
                        .anyMatch(product -> Objects.equals(product.getId(), productId)))
                .map(Promotion::getDiscountPercent)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));

        String oldStatus = order.getStatus();
        String newStatus = asString(req.get("status"));

        // --- VÁ LỖI 3: CỘNG LẠI KHO KHI HỦY/HOÀN ĐƠN ---
        if (oldStatus != null && !oldStatus.equals(newStatus) &&
                ("CANCELLED".equals(newStatus) || "RETURNED".equals(newStatus))) {

            for (OrderItem item : order.getOrderItems()) {
                ProductVariant variant = item.getVariant();
                if (variant != null) {
                    variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
                    variantRepo.save(variant);
                }
            }
        }
        // ----------------------------------------------

        if (newStatus != null)
            order.setStatus(newStatus);
        String pStatus = asString(req.get("paymentStatus"));
        String returnReason = asString(req.get("returnReason"));
        if (returnReason != null && !returnReason.isEmpty()) {
            order.setReturnReason(returnReason);
        }
        if (pStatus != null)
            order.setPaymentStatus(pStatus);

        orderRepo.save(order);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));
        return ResponseEntity.ok(mapOrderToMap(order));
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderRepo.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(orders.map(this::mapOrderToMap));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getOrdersByAccount(@PathVariable Integer accountId) {
        List<Order> list = orderRepo.findByAccountId(accountId);
        return ResponseEntity.ok(list.stream().map(this::mapOrderToMap).toList());
    }

    // --- HELPER METHODS ---
    private Map<String, Object> mapOrderToMap(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("status", order.getStatus());
        map.put("totalMoney", order.getTotalMoney());
        map.put("discountAmount", order.getDiscountAmount());
        map.put("finalAmount", order.getFinalAmount());
        map.put("fullname", order.getFullname());
        map.put("phone", order.getPhone());
        map.put("shippingAddress", order.getShippingAddress());
        map.put("paymentStatus", order.getPaymentStatus());
        map.put("returnReason", order.getReturnReason());
        map.put("createdAt", order.getCreatedAt());
        map.put("accountId", order.getAccount() != null ? order.getAccount().getId() : null);

        if (order.getPaymentMethod() != null) {
            map.put("paymentMethodName", order.getPaymentMethod().getName());
        }

        // --- BỔ SUNG: ĐÓNG GÓI DANH SÁCH SẢN PHẨM GỬI CHO FRONTEND ---
        List<Map<String, Object>> items = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem oi : order.getOrderItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", oi.getId());
                itemMap.put("quantity", oi.getQuantity());
                itemMap.put("price", oi.getPrice());

                ProductVariant variant = oi.getVariant();
                if (variant != null) {
                    itemMap.put("variantId", variant.getId());
                    if (variant.getProduct() != null) {
                        itemMap.put("productName", variant.getProduct().getName());
                        // Lấy ảnh của biến thể, nếu không có thì lấy ảnh gốc của sản phẩm
                        itemMap.put("imageUrl", variant.getImageUrl() != null ? variant.getImageUrl()
                                : variant.getProduct().getImageUrl());
                    }
                    if (variant.getColor() != null)
                        itemMap.put("colorName", variant.getColor().getName());
                    if (variant.getSize() != null)
                        itemMap.put("sizeName", variant.getSize().getName());
                }
                items.add(itemMap);
            }
        }

        // Gắn danh sách vào Response (Gắn cả 2 tên để lót đường cho mọi loại Frontend)
        map.put("items", items);
        map.put("orderItems", items);
        // --------------------------------------------------------------

        return map;
    }

    private void validateVoucherForOrder(Voucher voucher, BigDecimal orderTotal) {
        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            throw new BadRequestException("Mã giảm giá không còn hoạt động!");
        }

        Instant now = Instant.now();
        if (now.isBefore(voucher.getStartDate())) {
            throw new BadRequestException("Mã giảm giá chưa bắt đầu hoạt động");
        }
        if (now.isAfter(voucher.getEndDate())) {
            throw new BadRequestException("Mã giảm giá đã hết hạn");
        }

        int usedCount = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;
        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() > 0 && usedCount >= voucher.getUsageLimit()) {
            throw new BadRequestException("Mã giảm giá đã đạt giới hạn sử dụng");
        }

        if (orderTotal != null && voucher.getMinOrderValue() != null && orderTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new BadRequestException("Giá trị đơn hàng chưa đạt mức tối thiểu để sử dụng mã giảm giá");
        }
    }

    private BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderTotal) {
        if (voucher == null || orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if ("PERCENT".equals(voucher.getDiscountType())) {
            BigDecimal discount = orderTotal.multiply(voucher.getDiscountValue())
                    .divide(new BigDecimal(100), RoundingMode.DOWN);
            if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                return voucher.getMaxDiscountAmount();
            }
            return discount;
        }

        BigDecimal amountDiscount = voucher.getDiscountValue() != null ? voucher.getDiscountValue() : BigDecimal.ZERO;
        if (amountDiscount.compareTo(orderTotal) > 0) {
            return orderTotal;
        }
        return amountDiscount;
    }

    private List<Map<String, Object>> extractOrderItems(Map<String, Object> payload) {
        if (payload.get("items") instanceof List) {
            return (List<Map<String, Object>>) payload.get("items");
        }
        if (payload.get("cartItems") instanceof List) {
            return (List<Map<String, Object>>) payload.get("cartItems");
        }
        if (payload.get("cart") instanceof List) {
            return (List<Map<String, Object>>) payload.get("cart");
        }
        if (payload.get("orderItems") instanceof List) {
            return (List<Map<String, Object>>) payload.get("orderItems");
        }
        return null;
    }

    private Long extractVariantId(Map<String, Object> itemReq) {
        if (itemReq == null) {
            return null;
        }

        Long direct = asLong(itemReq.get("variantId"));
        if (direct != null) {
            return direct;
        }

        direct = asLong(itemReq.get("variant_id"));
        if (direct != null) {
            return direct;
        }

        direct = asLong(itemReq.get("productVariantId"));
        if (direct != null) {
            return direct;
        }

        direct = asLong(itemReq.get("product_variant_id"));
        if (direct != null) {
            return direct;
        }

        Object nested = itemReq.get("variant");
        if (nested instanceof Map<?, ?> nestedMap) {
            return asLong(nestedMap.get("id"));
        }

        return null;
    }

    private boolean isVariantSellable(ProductVariant variant) {
        if (variant == null) {
            return false;
        }

        String status = variant.getStatus();
        if (status == null || status.isBlank()) {
            return true;
        }

        String normalized = status.trim().toUpperCase();
        return !("HIDDEN".equals(normalized)
                || "INACTIVE".equals(normalized)
                || "STOP_SELLING".equals(normalized)
                || "DISCONTINUED".equals(normalized));
    }

    private Integer asInteger(Object v) {
        if (v == null)
            return null;
        return Integer.valueOf(v.toString());
    }

    private Long asLong(Object v) {
        if (v == null)
            return null;
        return Long.valueOf(v.toString());
    }

    private String asString(Object v) {
        return v == null ? null : v.toString();
    }

    private String firstNonBlank(Object... values) {
        if (values == null) {
            return null;
        }
        for (Object value : values) {
            String text = asString(value);
            if (text != null && !text.isBlank()) {
                return text.trim();
            }
        }
        return null;
    }

}
