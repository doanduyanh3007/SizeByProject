package com.finalproject2.service;

import com.finalproject2.model.request.SizeRequest;
import com.finalproject2.model.response.SizeResponse;

import java.util.List;

public interface SizeService {
    SizeResponse create(SizeRequest req);
    SizeResponse update(Long id, SizeRequest req);
    SizeResponse getById(Long id);
    List<SizeResponse> getAll();
    void delete(Long id);
}
