package com.finalproject2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "code")
    private String code;

    @Size(max = 20)
    @Column(name = "discount_type", length = 20)
    private String discountType;

    @NotNull
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @ColumnDefault("0")
    @Column(name = "min_order_value", precision = 12, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "max_discount_amount", precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @ColumnDefault("100")
    @Column(name = "usage_limit")
    private Integer usageLimit;

    @ColumnDefault("0")
    @Column(name = "used_count")
    private Integer usedCount;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "voucher")
    private Set<Order> orders = new LinkedHashSet<>();
}
