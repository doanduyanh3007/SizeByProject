package com.finalproject2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ProductVariants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "size_id", nullable = false)
    private com.finalproject2.entity.Size size;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @Column(name = "price", precision = 38, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "stock_Quantity", nullable = false)
    private Integer stockQuantity;

    @Size(max = 1000, message = "Link ảnh quá dài")
    @Column(name = "image_Url", length = 1000)
    private String imageUrl;

    @Size(max = 30)
    @ColumnDefault("SELLING")
    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // JsonIgnore để tránh circular reference:
    // ProductVariant → cartItems → CartItem → cart → Cart → cartItems → ...
    @JsonIgnore
    @OneToMany(mappedBy = "variant")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    // JsonIgnore để tránh circular reference:
    // ProductVariant → orderItems → OrderItem → order → Order → orderItems → ...
    @JsonIgnore
    @OneToMany(mappedBy = "variant")
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

    @Transient
    public Long getProductId() {
        return this.product != null ? this.product.getId() : null;
    }

    @Transient
    public Long getSizeId() {
        return this.size != null ? this.size.getId() : null;
    }

    @Transient
    public Long getColorId() {
        return this.color != null ? this.color.getId() : null;
    }

    @ElementCollection
    @CollectionTable(name = "VariantImages", joinColumns = @JoinColumn(name = "variant_id"))
    @Column(name = "image_url")
    private List<String> extraImages;
}
