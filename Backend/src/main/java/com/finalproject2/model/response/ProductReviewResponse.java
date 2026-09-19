// language: java
package com.finalproject2.model.response;

import com.finalproject2.entity.ProductReview;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ProductReviewResponse {
    private Long id;
    private Long productId;
    private Integer accountId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ProductReviewResponse() {}

    public ProductReviewResponse(ProductReview r) {
        this.id = r.getId();
        this.productId = r.getProduct() != null ? r.getProduct().getId() : null;
        this.accountId = r.getAccount() != null ? r.getAccount().getId() : null;
        this.rating = r.getRating();
        this.comment = r.getComment();
        Instant instant = r.getCreatedAt();
        if (instant != null) {
            this.createdAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } else {
            this.createdAt = null;
        }
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
