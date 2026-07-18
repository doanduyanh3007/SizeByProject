package com.finalproject2.controller;

import com.finalproject2.model.request.SizeRequest;
import com.finalproject2.model.response.SizeResponse;
import com.finalproject2.service.SizeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@CrossOrigin(originPatterns = "*")
public class SizeController {

    private final SizeService sizeService;

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    @PostMapping
    public ResponseEntity<SizeResponse> create(@Valid @RequestBody SizeRequest req) {
        return ResponseEntity.ok(sizeService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<SizeResponse>> getAll() {
        return ResponseEntity.ok(sizeService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<SizeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sizeService.getById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<SizeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SizeRequest req) {
        return ResponseEntity.ok(sizeService.update(id, req));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sizeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
