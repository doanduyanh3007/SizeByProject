package com.finalproject2.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalproject2.entity.ProductVariant;
import com.finalproject2.model.request.ProductVariantRequest;
import com.finalproject2.model.response.ProductVariantResponse;
import com.finalproject2.service.ProductVariantService;
import com.finalproject2.repository.ProductVariantRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/variants")
@CrossOrigin(originPatterns = "*")
public class ProductVariantController {
    private final ProductVariantService svc;
    private final ProductVariantRepository repo;
    private final Cloudinary cloudinary;

    public ProductVariantController(ProductVariantService svc, ProductVariantRepository repo, Cloudinary cloudinary) {
        this.svc = svc;
        this.repo = repo;
        this.cloudinary = cloudinary;
    }

    @PostMapping
    public ResponseEntity<ProductVariantResponse> create(@Valid @RequestBody ProductVariantRequest req) {
        return ResponseEntity.ok(svc.create(req));
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantResponse>> getAll(
            @RequestParam(required = false) Long productId) {
        if (productId != null) {
            return ResponseEntity.ok(svc.getByProductId(productId));
        }
        return ResponseEntity.ok(svc.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(svc.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantResponse> update(@PathVariable Long id, @Valid @RequestBody ProductVariantRequest req) {
        return ResponseEntity.ok(svc.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- API UPLOAD ẢNH CHO BIẾN THỂ LÊN CLOUDINARY ---
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadVariantImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageType", defaultValue = "primary") String imageType,
            @RequestParam(value = "index", required = false) Integer index) {
        try {
            ProductVariant variant = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Kh├┤ng t├¼m thß║Ñy biß║┐n thß╗â"));

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("secure_url").toString();

            if ("extra".equals(imageType)) {
                // Add to extraImages
                if (variant.getExtraImages() == null) {
                    variant.setExtraImages(new java.util.ArrayList<>());
                }

                if (index != null && index >= 0 && index < 4) {
                    while (variant.getExtraImages().size() <= index) {
                        variant.getExtraImages().add(null);
                    }
                    variant.getExtraImages().set(index, imageUrl);
                } else {
                    if (variant.getExtraImages().size() < 4) {
                        variant.getExtraImages().add(imageUrl);
                    }
                }
            } else {
                // Set as primary image
                variant.setImageUrl(imageUrl);
            }

            repo.save(variant);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "imageUrl", imageUrl,
                    "extraImages", variant.getExtraImages()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/{id}/add-extra-image")
    public ResponseEntity<?> addExtraImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "index", required = false, defaultValue = "-1") Integer index) {
        try {
            ProductVariant variant = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("secure_url").toString();

            // Initialize extraImages if not present
            if (variant.getExtraImages() == null) {
                variant.setExtraImages(new java.util.ArrayList<>());
            }

            // Add or update at specific index
            if (index >= 0 && index < 4) {
                // Pad with nulls if needed to reach that index
                while (variant.getExtraImages().size() <= index) {
                    variant.getExtraImages().add(null);
                }
                variant.getExtraImages().set(index, imageUrl);
            } else {
                // Append to the end (max 4 extra images)
                if (variant.getExtraImages().size() < 4) {
                    variant.getExtraImages().add(imageUrl);
                }
            }

            repo.save(variant);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "imageUrl", imageUrl,
                    "extraImages", variant.getExtraImages()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @DeleteMapping("/{id}/extra-images/{index}")
    public ResponseEntity<?> removeExtraImage(
            @PathVariable Long id,
            @PathVariable Integer index) {
        try {
            ProductVariant variant = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

            if (variant.getExtraImages() != null && index >= 0 && index < variant.getExtraImages().size()) {
                variant.getExtraImages().remove((int) index);
                repo.save(variant);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "extraImages", variant.getExtraImages() != null ? variant.getExtraImages() : new java.util.ArrayList<>()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{id}/extra-images")
    public ResponseEntity<?> updateExtraImages(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            ProductVariant variant = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

            @SuppressWarnings("unchecked")
            List<String> extraImages = (List<String>) payload.get("extraImages");

            variant.setExtraImages(extraImages);
            repo.save(variant);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "extraImages", variant.getExtraImages()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}