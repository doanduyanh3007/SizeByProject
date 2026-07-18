package com.finalproject2.service;

import com.finalproject2.entity.Account;
import com.finalproject2.entity.Product;
import com.finalproject2.entity.ProductReview;
import com.finalproject2.model.request.ProductReviewRequest;
import com.finalproject2.repository.AccountRepository;
import com.finalproject2.repository.ProductRepository;
import com.finalproject2.repository.ProductReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository reviewRepo;
    private final ProductRepository productRepo;
    private final AccountRepository accountRepo;

    public ProductReviewServiceImpl(ProductReviewRepository reviewRepo,
                                    ProductRepository productRepo,
                                    AccountRepository accountRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    public Page<ProductReview> findAll(Long productId, Pageable pageable) {
        if (productId != null) {
            return reviewRepo.findByProductId(productId, pageable);
        }
        return reviewRepo.findAll(pageable);
    }

    @Override
    public Optional<ProductReview> findById(Long id) {
        return reviewRepo.findById(id);
    }

    @Override
    public ProductReview create(ProductReview review) {
        if (review.getProduct() == null || review.getProduct().getId() == null) {
            throw new IllegalArgumentException("productId is required");
        }
        if (review.getAccount() == null || review.getAccount().getId() == null) {
            throw new IllegalArgumentException("accountId is required");
        }

        Product product = productRepo.findById(review.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id " + review.getProduct().getId()));
        Account account = accountRepo.findById(review.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id " + review.getAccount().getId()));

        review.setProduct(product);
        review.setAccount(account);

        if (review.getCreatedAt() == null) {
            review.setCreatedAt(Instant.now());
        }

        return reviewRepo.save(review);
    }

    @Override
    public Optional<ProductReview> update(Long id, ProductReviewRequest req) {
        return reviewRepo.findById(id).map(existing -> {
            if (req.getProductId() != null && !req.getProductId().equals(existing.getProduct().getId())) {
                Product product = productRepo.findById(req.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found with id " + req.getProductId()));
                existing.setProduct(product);
            }

            if (req.getAccountId() != null && !req.getAccountId().equals(existing.getAccount().getId())) {
                Account account = accountRepo.findById(req.getAccountId())
                        .orElseThrow(() -> new IllegalArgumentException("Account not found with id " + req.getAccountId()));
                existing.setAccount(account);
            }

            existing.setRating(req.getRating());
            existing.setComment(req.getComment());
            return reviewRepo.save(existing);
        });
    }

    @Override
    public boolean delete(Long id) {
        if (!reviewRepo.existsById(id)) return false;
        reviewRepo.deleteById(id);
        return true;
    }
}
