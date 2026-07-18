package com.finalproject2.repository;

import com.finalproject2.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Page<ProductReview> findByProductId(Long productId, Pageable pageable);
    Optional<ProductReview> findByProductIdAndAccountId(Long productId, Integer accountId);
}
