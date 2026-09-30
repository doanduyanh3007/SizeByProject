package com.finalproject2.service;

import com.finalproject2.entity.Brand;
import com.finalproject2.exception.NotFoundException;
import com.finalproject2.mapper.MapperUtil;
import com.finalproject2.model.request.BrandRequest;
import com.finalproject2.model.response.BrandResponse;
import com.finalproject2.repository.BrandRepository;
import com.finalproject2.service.BrandService;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import com.finalproject2.exception.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {
    private final BrandRepository repo;

    public BrandServiceImpl(BrandRepository repo) { this.repo = repo; }

    @Override
    public BrandResponse create(BrandRequest req) {
        Brand b = new Brand();
        MapperUtil.mapToExisting(req, b);
        b = repo.save(b);
        return MapperUtil.map(b, BrandResponse.class);
    }

    @Override
    public BrandResponse update(Long id, BrandRequest req) {
        Brand b = repo.findById(id).orElseThrow(() -> new NotFoundException("Brand not found"));
        MapperUtil.mapToExisting(req, b);
        b = repo.save(b);
        return MapperUtil.map(b, BrandResponse.class);
    }

    @Override
    public BrandResponse getById(Long id) {
        Brand b = repo.findById(id).orElseThrow(() -> new NotFoundException("Brand not found"));
        return MapperUtil.map(b, BrandResponse.class);
    }

    @Override
    public List<BrandResponse> getAll() {
        return repo.findAll().stream()
                .map(e -> MapperUtil.map(e, BrandResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Brand b = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thương hiệu"));
        b.setIsDeleted(!Boolean.TRUE.equals(b.getIsDeleted()));
        repo.save(b);
    }
}
