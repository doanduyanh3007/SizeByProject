package com.finalproject2.model.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductVariantResponse {

    private Long id;
    private Long productId;
    private Long sizeId;
    private Long colorId;
    private String colorName;
    private String sizeName;
    private BigDecimal price;
    private Integer stockQuantity;
    private String status;
    private String imageUrl;
    private List<String> extraImages;
}
