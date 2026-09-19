package com.finalproject2.service;

import com.finalproject2.model.request.BrandRequest;
import com.finalproject2.model.response.BrandResponse;

import java.util.List;

public interface BrandService {
    BrandResponse create(BrandRequest req);
    BrandResponse update(Long id, BrandRequest req);
    BrandResponse getById(Long id);
    List<BrandResponse> getAll();
    void delete(Long id);
}