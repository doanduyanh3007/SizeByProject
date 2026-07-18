// java
package com.finalproject2.service;

import com.finalproject2.model.request.ProductRequest;
import com.finalproject2.model.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List; // Thêm import List

public interface ProductService {
    ProductResponse create(ProductRequest req);
    ProductResponse update(Long id, ProductRequest req);
    ProductResponse getById(Long id);
    List<ProductResponse> getAll();
    void delete(Long id);
}
