package com.finalproject2.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductVariantRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long sizeId;
    @NotNull
    private Long colorId;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Integer stockQuantity;
    private String status;
    private String imageUrl;
    private List<String> extraImages;  // NEW: for 4 additional images
}
