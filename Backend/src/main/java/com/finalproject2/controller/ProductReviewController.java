// language: java
package com.finalproject2.controller;

import com.finalproject2.entity.Product;
import com.finalproject2.entity.Account;
import com.finalproject2.entity.ProductReview;
import com.finalproject2.model.request.ProductReviewRequest;
import com.finalproject2.model.response.ProductReviewResponse;
import com.finalproject2.repository.ProductReviewRepository;
import com.finalproject2.repository.ProductRepository;
import com.finalproject2.repository.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@Transactional
public class ProductReviewController {

    private final ProductReviewRepository reviewRepo;
    private final ProductRepository productRepo;
    private final AccountRepository accountRepo;

    public ProductReviewController(ProductReviewRepository reviewRepo,
                                   ProductRepository productRepo,
                                   AccountRepository accountRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
        this.accountRepo = accountRepo;
    }

    @GetMapping
    public ResponseEntity<Page<ProductReviewResponse>> getAll(
            @RequestParam(name = "productId", required = false) Long productId,
            Pageable pageable) {

        Page<ProductReview> page;
        if (productId != null) {
            page = reviewRepo.findByProductId(productId, pageable);
        } else {
            page = reviewRepo.findAll(pageable);
        }
        Page<ProductReviewResponse> dtoPage = page.map(ProductReviewResponse::new);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductReviewResponse> getById(@PathVariable Long id) {
        Optional<ProductReview> opt = reviewRepo.findById(id);
        return opt.map(r -> ResponseEntity.ok(new ProductReviewResponse(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProductReviewRequest req) {
        Optional<Product> optProduct = productRepo.findById(req.getProductId());
        if (optProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Product with id " + req.getProductId() + " not found");
        }
        // use Integer directly (accountId is Integer in request)
        Optional<Account> optAccount = accountRepo.findById(req.getAccountId());
        if (optAccount.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Account with id " + req.getAccountId() + " not found");
        }
        Optional<ProductReview> existingReview = reviewRepo.findByProductIdAndAccountId(
                req.getProductId(),
                req.getAccountId()
        );

        if (existingReview.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "DUPLICATE_REVIEW");
            response.put("message", "You have already reviewed this product");
            response.put("existingReview", new ProductReviewResponse(existingReview.get()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        ProductReview review = new ProductReview();
        review.setProduct(optProduct.get());
        review.setAccount(optAccount.get());
        review.setRating(req.getRating());
        review.setComment(req.getComment());

        ProductReview saved = reviewRepo.save(review);
        ProductReviewResponse resp = new ProductReviewResponse(saved);
        return ResponseEntity.created(URI.create("/api/reviews/" + saved.getId())).body(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProductReviewRequest req) {
        Optional<ProductReview> opt = reviewRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ProductReview existing = opt.get();

        // Optionally allow changing product/account if desired:
        if (req.getProductId() != null && !req.getProductId().equals(existing.getProduct().getId())) {
            Optional<Product> optProduct = productRepo.findById(req.getProductId());
            if (optProduct.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Product with id " + req.getProductId() + " not found");
            }
            existing.setProduct(optProduct.get());
        }
        if (req.getAccountId() != null && !req.getAccountId().equals(existing.getAccount().getId())) {
            Optional<Account> optAccount = accountRepo.findById(req.getAccountId());
            if (optAccount.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Account with id " + req.getAccountId() + " not found");
            }
            existing.setAccount(optAccount.get());
        }

        existing.setRating(req.getRating());
        existing.setComment(req.getComment());

        ProductReview saved = reviewRepo.save(existing);
        return ResponseEntity.ok(new ProductReviewResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reviewRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
