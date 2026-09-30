package com.finalproject2.controller;

import com.finalproject2.entity.ProductSerial;
import com.finalproject2.repository.ProductSerialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/serials")
@CrossOrigin(originPatterns = "*")
public class ProductSerialController {

    private final ProductSerialRepository serialRepo;

    public ProductSerialController(ProductSerialRepository serialRepo) {
        this.serialRepo = serialRepo;
    }

    @GetMapping("/variant/{variantId}")
    public ResponseEntity<?> getAvailableSerials(@PathVariable Long variantId) {
        List<ProductSerial> serials = serialRepo.findAllAvailableSerials(variantId);
        List<Map<String, Object>> result = serials.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("serialCode", s.getSerialCode());
            map.put("status", s.getStatus());
            map.put("variantId", s.getVariant().getId());
            map.put("createdAt", s.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
