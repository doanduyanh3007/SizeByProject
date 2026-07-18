package com.finalproject2.service;

import com.finalproject2.model.request.ColorRequest;
import com.finalproject2.model.response.ColorResponse;

import java.util.List;

public interface ColorService {
    ColorResponse create(ColorRequest req);
    ColorResponse update(Long id, ColorRequest req);
    ColorResponse getById(Long id);
    List<ColorResponse> getAll();
    void delete(Long id);
}
