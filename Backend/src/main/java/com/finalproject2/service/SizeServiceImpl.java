package com.finalproject2.service;

import com.finalproject2.entity.Size;
import com.finalproject2.exception.BadRequestException;
import com.finalproject2.exception.NotFoundException;
import com.finalproject2.mapper.MapperUtil;
import com.finalproject2.model.request.SizeRequest;
import com.finalproject2.model.response.SizeResponse;
import com.finalproject2.repository.SizeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeServiceImpl implements SizeService {
    private final SizeRepository repo;

    public SizeServiceImpl(SizeRepository repo) {
        this.repo = repo;
    }

    @Override
    public SizeResponse create(SizeRequest req) {
        Size size = new Size();
        MapperUtil.mapToExisting(req, size);
        size = repo.save(size);
        return MapperUtil.map(size, SizeResponse.class);
    }

    @Override
    public SizeResponse update(Long id, SizeRequest req) {
        Size size = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy size"));
        MapperUtil.mapToExisting(req, size);
        size = repo.save(size);
        return MapperUtil.map(size, SizeResponse.class);
    }

    @Override
    public SizeResponse getById(Long id) {
        Size size = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy size"));
        return MapperUtil.map(size, SizeResponse.class);
    }

    @Override
    public List<SizeResponse> getAll() {
        return repo.findAll().stream()
                .map(size -> MapperUtil.map(size, SizeResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Size size = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy size"));
        size.setIsDeleted(!Boolean.TRUE.equals(size.getIsDeleted()));
        repo.save(size);
    }
}
