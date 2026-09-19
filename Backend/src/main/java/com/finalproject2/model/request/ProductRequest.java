package com.finalproject2.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String productCode;
    private String description;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long brandId;
    @Size(max = 2048)
    private String imageUrl;
    private String status;
    private String galleryImages;
}