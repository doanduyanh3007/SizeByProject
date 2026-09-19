package com.finalproject2.controller;

import com.finalproject2.entity.*;
import com.finalproject2.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final AccountRepository accountRepo;
    private final ProductVariantRepository variantRepo;

    public CartController(CartRepository cartRepo,
                          CartItemRepository cartItemRepo,
                          AccountRepository accountRepo,
                          ProductVariantRepository variantRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.accountRepo = accountRepo;
        this.variantRepo = variantRepo;
    }

    /**
     * Ensure cart exists for account (creates if missing)
     * POST /api/cart/init
     * { "accountId": 2 }
     */
    @PostMapping("/init")
    @Transactional
    public ResponseEntity<?> initializeCart(@RequestBody Map<String, Object> payload) {
        try {
            Integer accountId = Integer.valueOf(payload.get("accountId").toString());

            Account account = accountRepo.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

            Cart cart = cartRepo.findByAccountId(accountId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setAccount(account);
                        newCart.setCartItems(new LinkedHashSet<>());
                        return cartRepo.save(newCart);
                    });

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Giỏ hàng đã được khởi tạo",
                    "cartId", cart.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get cart items for user
     * GET /api/cart?accountId=2
     */
    @GetMapping
    public ResponseEntity<?> getCart(@RequestParam Integer accountId) {
        try {
            Account account = accountRepo.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

            // Return empty cart if doesn't exist (don't error)
            Cart cart = cartRepo.findByAccountId(accountId).orElse(null);

            List<Map<String, Object>> items = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            if (cart != null) {
                for (CartItem item : cart.getCartItems()) {
                    Map<String, Object> itemMap = new HashMap<>();
                    ProductVariant variant = item.getVariant();
                    Product product = variant.getProduct();

                    itemMap.put("cartItemId", item.getId());
                    itemMap.put("variantId", variant.getId());
                    itemMap.put("productId", product.getId());
                    itemMap.put("productName", product.getName());
                    itemMap.put("productCode", product.getProductCode());
                    itemMap.put("price", variant.getPrice());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("subtotal", variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    itemMap.put("stockAvailable", variant.getStockQuantity());

                    // Add variant details
                    itemMap.put("size", variant.getSize().getName());
                    itemMap.put("sizeId", variant.getSize().getId());
                    itemMap.put("color", variant.getColor().getName());
                    itemMap.put("colorId", variant.getColor().getId());
                    String imageUrl = variant.getImageUrl() != null ? variant.getImageUrl() : product.getImageUrl();
                    itemMap.put("image", imageUrl);
                    itemMap.put("imageUrl", imageUrl);

                    items.add(itemMap);
                    totalPrice = totalPrice.add(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cartId", cart != null ? cart.getId() : null);
            response.put("items", items);
            response.put("totalPrice", totalPrice);
            response.put("itemCount", items.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Add item to cart or update quantity
     * AUTO-CREATES CART IF IT DOESN'T EXIST
     * POST /api/cart/add
     * {
     *   "accountId": 2,
     *   "variantId": 1,
     *   "quantity": 1
     * }
     */
    @PostMapping("/add")
    @Transactional
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> payload) {
        try {
            Integer accountId = Integer.valueOf(payload.get("accountId").toString());
            Long variantId = Long.valueOf(payload.get("variantId").toString());
            Integer quantity = Integer.valueOf(payload.get("quantity").toString());

            if (quantity <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Số lượng phải lớn hơn 0"
                ));
            }

            Account account = accountRepo.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

            ProductVariant variant = variantRepo.findById(variantId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            // Auto-create cart if missing (KEY FIX)
            Cart cart = cartRepo.findByAccountId(accountId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setAccount(account);
                        newCart.setCartItems(new LinkedHashSet<>());
                        return cartRepo.save(newCart);
                    });

            // Check if variant already in cart
            CartItem existingItem = cart.getCartItems().stream()
                    .filter(ci -> ci.getVariant().getId().equals(variantId))
                    .findFirst()
                    .orElse(null);

            int totalRequested = quantity;
            if (existingItem != null) {
                totalRequested += existingItem.getQuantity();
            }

            if (variant.getStockQuantity() < totalRequested) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Sản phẩm không có đủ số lượng. Số lượng tồn kho: " + variant.getStockQuantity()
                ));
            }

            if (existingItem != null) {
                // Update quantity
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                cartItemRepo.save(existingItem);
            } else {
                // Create new cart item
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setVariant(variant);
                cartItem.setQuantity(quantity);
                cartItemRepo.save(cartItem);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thêm vào giỏ hàng thành công!"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Update cart item quantity
     * PUT /api/cart/update/{cartItemId}
     * {
     *   "quantity": 2
     * }
     */
    @PutMapping("/update/{cartItemId}")
    @Transactional
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId,
                                            @RequestBody Map<String, Object> payload) {
        try {
            Integer quantity = Integer.valueOf(payload.get("quantity").toString());

            if (quantity <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Số lượng phải lớn hơn 0"
                ));
            }

            CartItem cartItem = cartItemRepo.findById(cartItemId)
                    .orElseThrow(() -> new RuntimeException("Mục giỏ hàng không tồn tại"));

            ProductVariant variant = cartItem.getVariant();
            if (variant.getStockQuantity() < quantity) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Sản phẩm không có đủ số lượng. Còn lại: " + variant.getStockQuantity()
                ));
            }

            cartItem.setQuantity(quantity);
            cartItemRepo.save(cartItem);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật giỏ hàng thành công!"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Remove item from cart
     * DELETE /api/cart/remove/{cartItemId}
     */
    @DeleteMapping("/remove/{cartItemId}")
    @Transactional
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId) {
        try {
            CartItem cartItem = cartItemRepo.findById(cartItemId)
                    .orElseThrow(() -> new RuntimeException("Mục giỏ hàng không tồn tại"));

            cartItemRepo.delete(cartItem);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa khỏi giỏ hàng thành công!"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Clear entire cart
     * DELETE /api/cart/clear?accountId=2
     */
    @DeleteMapping("/clear")
    @Transactional
    public ResponseEntity<?> clearCart(@RequestParam Integer accountId) {
        try {
            Cart cart = cartRepo.findByAccountId(accountId)
                    .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));

            cartItemRepo.deleteByCartId(cart.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Giỏ hàng đã được xóa trống!"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}