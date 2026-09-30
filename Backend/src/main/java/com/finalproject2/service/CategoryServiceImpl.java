package com.finalproject2.service;

import com.finalproject2.entity.Category;
import com.finalproject2.exception.NotFoundException;
import com.finalproject2.mapper.MapperUtil;
import com.finalproject2.model.request.CategoryRequest;
import com.finalproject2.model.response.CategoryResponse;
import com.finalproject2.repository.CategoryRepository;
import com.finalproject2.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import com.finalproject2.exception.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;

    public CategoryServiceImpl(CategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public CategoryResponse create(CategoryRequest req) {
        Category c = new Category();
        MapperUtil.mapToExisting(req, c);
        c = repo.save(c);
        return MapperUtil.map(c, CategoryResponse.class);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest req) {
        Category c = repo.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        MapperUtil.mapToExisting(req, c);
        c = repo.save(c);
        return MapperUtil.map(c, CategoryResponse.class);
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category c = repo.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        return MapperUtil.map(c, CategoryResponse.class);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return repo.findAll().stream()
                .map(e -> MapperUtil.map(e, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục"));
        c.setIsDeleted(!Boolean.TRUE.equals(c.getIsDeleted()));
        repo.save(c);
    }
}
