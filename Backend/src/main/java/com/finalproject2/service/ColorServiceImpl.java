package com.finalproject2.service;

import com.finalproject2.entity.Color;
import com.finalproject2.exception.BadRequestException;
import com.finalproject2.exception.NotFoundException;
import com.finalproject2.mapper.MapperUtil;
import com.finalproject2.model.request.ColorRequest;
import com.finalproject2.model.response.ColorResponse;
import com.finalproject2.repository.ColorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColorServiceImpl implements ColorService {
    private final ColorRepository repo;

    public ColorServiceImpl(ColorRepository repo) {
        this.repo = repo;
    }

    @Override
    public ColorResponse create(ColorRequest req) {
        Color color = new Color();
        MapperUtil.mapToExisting(req, color);
        color = repo.save(color);
        return MapperUtil.map(color, ColorResponse.class);
    }

    @Override
    public ColorResponse update(Long id, ColorRequest req) {
        Color color = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy màu sắc"));
        MapperUtil.mapToExisting(req, color);
        color = repo.save(color);
        return MapperUtil.map(color, ColorResponse.class);
    }

    @Override
    public ColorResponse getById(Long id) {
        Color color = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy màu sắc"));
        return MapperUtil.map(color, ColorResponse.class);
    }

    @Override
    public List<ColorResponse> getAll() {
        return repo.findAll().stream()
                .map(color -> MapperUtil.map(color, ColorResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Color color = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy màu sắc"));
        try {
            repo.delete(color);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Không thể xóa! Đang có biến thể sử dụng màu này.");
        }
    }
}
