package com.finalproject2.controller;

import com.finalproject2.model.request.ColorRequest;
import com.finalproject2.model.response.ColorResponse;
import com.finalproject2.service.ColorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@CrossOrigin(originPatterns = "*")
public class ColorController {

    private final ColorService colorService;

    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    @PostMapping
    public ResponseEntity<ColorResponse> create(@Valid @RequestBody ColorRequest req) {
        return ResponseEntity.ok(colorService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<ColorResponse>> getAll() {
        return ResponseEntity.ok(colorService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<ColorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(colorService.getById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<ColorResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ColorRequest req) {
        return ResponseEntity.ok(colorService.update(id, req));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        colorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
