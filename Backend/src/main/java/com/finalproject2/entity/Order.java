package com.finalproject2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Nationalized
    @Size(max = 255)
    @Column(name = "fullname", columnDefinition = "nvarchar(255)")
    private String fullname;

    @Size(max = 255)
    @Column(name = "phone")
    private String phone;

    @NotNull
    @Nationalized
    @Lob
    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @NotNull
    @Column(name = "total_money", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalMoney;

    @ColumnDefault("0")
    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @NotNull
    @Column(name = "final_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Size(max = 20)
    @ColumnDefault("PENDING")
    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Size(max = 255)
    @ColumnDefault("NEW")
    @Column(name = "status")
    private String status;

    @ColumnDefault("getdate()")
    @Column(name = "created_at")
    private Instant createdAt;

    @Size(max = 100)
    @Column(name = "email")
    private String email;

    @Nationalized
    @Column(name = "return_reason")
    private String returnReason;

    @Column(name = "return_evidence_images", length = 1000)
    private String returnEvidenceImages;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new LinkedHashSet<>();
}
