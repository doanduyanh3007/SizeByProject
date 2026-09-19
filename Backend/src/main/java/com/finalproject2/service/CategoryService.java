package com.finalproject2.service;

import com.finalproject2.model.request.CategoryRequest;
import com.finalproject2.model.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest req);
    CategoryResponse update(Long id, CategoryRequest req);
    CategoryResponse getById(Long id);
    List<CategoryResponse> getAll();
    void delete(Long id);
}