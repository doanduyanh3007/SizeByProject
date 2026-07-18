package com.finalproject2.service;

import com.finalproject2.model.request.ProductVariantRequest;
import com.finalproject2.model.response.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {
    ProductVariantResponse create(ProductVariantRequest req);
    ProductVariantResponse update(Long id, ProductVariantRequest req);
    ProductVariantResponse getById(Long id);
    List<ProductVariantResponse> getAll();
    List<ProductVariantResponse> getByProductId(Long productId);
    void delete(Long id);
}