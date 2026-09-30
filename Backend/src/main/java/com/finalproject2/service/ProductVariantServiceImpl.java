package com.finalproject2.service;

import com.finalproject2.entity.Color;
import com.finalproject2.entity.Product;
import com.finalproject2.entity.ProductVariant;
import com.finalproject2.entity.Size;
import com.finalproject2.exception.NotFoundException;
import com.finalproject2.exception.BadRequestException;
import com.finalproject2.model.request.ProductVariantRequest;
import com.finalproject2.model.response.ProductVariantResponse;
import com.finalproject2.repository.ColorRepository;
import com.finalproject2.repository.ProductRepository;
import com.finalproject2.repository.ProductVariantRepository;
import com.finalproject2.repository.SizeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository repo;
    private final ProductRepository productRepo;
    private final SizeRepository sizeRepo;
    private final ColorRepository colorRepo;

    public ProductVariantServiceImpl(
            ProductVariantRepository repo,
            ProductRepository productRepo,
            SizeRepository sizeRepo,
            ColorRepository colorRepo) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.sizeRepo = sizeRepo;
        this.colorRepo = colorRepo;
    }

    @Override
    public ProductVariantResponse create(ProductVariantRequest req) {
        if (repo.existsByProduct_IdAndColor_IdAndSize_Id(req.getProductId(), req.getColorId(), req.getSizeId())) {
            throw new BadRequestException("Biến thể với màu và kích cỡ này đã tồn tại cho sản phẩm.");
        }

        Product p = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));
        Size s = sizeRepo.findById(req.getSizeId())
                .orElseThrow(() -> new NotFoundException("Size not found"));
        Color c = colorRepo.findById(req.getColorId())
                .orElseThrow(() -> new NotFoundException("Color not found"));

        ProductVariant v = new ProductVariant();
        v.setProduct(p);
        v.setSize(s);
        v.setColor(c);
        v.setPrice(req.getPrice());
        v.setStockQuantity(req.getStockQuantity());
        v.setStatus(normalizeStatus(req.getStatus(), req.getStockQuantity()));
        v.setImageUrl(req.getImageUrl());

        // NEW: Handle extraImages
        if (req.getExtraImages() != null && !req.getExtraImages().isEmpty()) {
            v.setExtraImages(req.getExtraImages());
        }

        v = repo.save(v);
        return toResponse(v);
    }

    @Override
    public ProductVariantResponse update(Long id, ProductVariantRequest req) {
        ProductVariant v = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        if (req.getProductId() != null) {
            v.setProduct(productRepo.findById(req.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found")));
        }
        if (req.getSizeId() != null) {
            v.setSize(sizeRepo.findById(req.getSizeId())
                    .orElseThrow(() -> new NotFoundException("Size not found")));
        }
        if (req.getColorId() != null) {
            v.setColor(colorRepo.findById(req.getColorId())
                    .orElseThrow(() -> new NotFoundException("Color not found")));
        }
        if (req.getPrice() != null) {
            v.setPrice(req.getPrice());
        }
        if (req.getStockQuantity() != null) {
            int oldStock = v.getStockQuantity() != null ? v.getStockQuantity() : 0;
            int newStock = req.getStockQuantity();
            v.setStockQuantity(newStock);

            if (req.getStatus() != null) {
                // Admin explicitly set a status → apply it (normalized)
                v.setStatus(normalizeStatus(req.getStatus(), newStock));
            } else {
                // Auto-resolve status based on stock change
                String currentStatus = v.getStatus() != null ? v.getStatus().trim().toUpperCase() : "";
                if (newStock <= 0) {
                    // Stock hits zero → out of stock (unless already hidden/stop_selling)
                    if (!"HIDDEN".equals(currentStatus) && !"STOP_SELLING".equals(currentStatus)) {
                        v.setStatus("OUT_OF_STOCK");
                    }
                } else if (oldStock <= 0 && newStock > 0) {
                    // Stock restored from zero:
                    // - If was OUT_OF_STOCK → auto switch to SELLING
                    // - If was STOP_SELLING / HIDDEN → keep as-is (admin paused intentionally)
                    if ("OUT_OF_STOCK".equals(currentStatus) || currentStatus.isEmpty()) {
                        v.setStatus("SELLING");
                    }
                    // else: STOP_SELLING / HIDDEN → unchanged
                }
                // If both old and new stock > 0 and no explicit status → keep current status
            }
        } else if (req.getStatus() != null) {
            v.setStatus(normalizeStatus(req.getStatus(), v.getStockQuantity()));
        }
        if (req.getImageUrl() != null) {
            v.setImageUrl(req.getImageUrl());
        }

        // NEW: Handle extraImages
        if (req.getExtraImages() != null) {
            v.setExtraImages(req.getExtraImages());
        }

        v = repo.save(v);
        return toResponse(v);
    }

    @Override
    public ProductVariantResponse getById(Long id) {
        ProductVariant v = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found"));
        return toResponse(v);
    }

    @Override
    public List<ProductVariantResponse> getAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductVariantResponse> getByProductId(Long productId) {
        return repo.findByProduct_Id(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
        public void delete(Long id) {
        ProductVariant v = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        String newStatus = "HIDDEN".equals(v.getStatus()) ? "SELLING" : "HIDDEN";
        v.setStatus(newStatus);
        v.setUpdatedAt(Instant.now());
        repo.save(v);
    }

    /**
     * Map thủ công từ entity sang response.
     * KHÔNG dùng ModelMapper để tránh circular reference:
     * ProductVariant → cartItems → CartItem → cart → Cart → cartItems → ...
     */
    private ProductVariantResponse toResponse(ProductVariant v) {
        ProductVariantResponse r = new ProductVariantResponse();
        r.setId(v.getId());
        r.setProductId(v.getProduct() != null ? v.getProduct().getId() : null);
        r.setSizeId(v.getSize()       != null ? v.getSize().getId()    : null);
        r.setColorId(v.getColor()     != null ? v.getColor().getId()   : null);
        r.setColorName(v.getColor()   != null ? v.getColor().getName() : null);
        r.setSizeName(v.getSize()     != null ? v.getSize().getName()  : null);
        r.setPrice(v.getPrice());
        r.setStockQuantity(v.getStockQuantity());
        r.setStatus(normalizeStatus(v.getStatus(), v.getStockQuantity()));
        r.setImageUrl(v.getImageUrl());
        r.setExtraImages(v.getExtraImages());
        return r;
    }

    private String normalizeStatus(String value, Integer stockQuantity) {
        String normalized = value == null ? "" : value.trim().toUpperCase();
        boolean hasStock = stockQuantity != null && stockQuantity > 0;

        // Admin explicitly stopped selling → always preserve, regardless of stock
        if ("STOP_SELLING".equals(normalized) || "NGUNG_BAN".equals(normalized)) {
            return "STOP_SELLING";
        }
        if ("HIDDEN".equals(normalized) || "INACTIVE".equals(normalized)) {
            return "HIDDEN";
        }

        // For OUT_OF_STOCK, SELLING, or unknown → always derive from actual stock
        // This ensures: stock=10 + status=OUT_OF_STOCK → displays as SELLING
        //               stock=0  + status=SELLING       → displays as OUT_OF_STOCK
        return hasStock ? "SELLING" : "OUT_OF_STOCK";
    }
}
