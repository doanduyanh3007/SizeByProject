// java
package com.finalproject2.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String productCode;
    private String description;
    private Long categoryId;
    private Long brandId;
    private String status;
    private String imageUrl;
    private Instant updatedAt;
    private Double averageRating;
    private Integer reviewCount;
    private String galleryImages;
}
