 package com.finalproject2.service;

import com.finalproject2.entity.ProductReview;
import com.finalproject2.model.request.ProductReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductReviewService {
    Page<ProductReview> findAll(Long productId, Pageable pageable);
    Optional<ProductReview> findById(Long id);
    ProductReview create(ProductReview review);
    Optional<ProductReview> update(Long id, ProductReviewRequest req);
    boolean delete(Long id);
}
