package com.finalproject2.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "AccountVouchers")
@Data
public class AccountVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account; // Phải khớp với entity Account của Duy

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher; // Phải khớp với entity Voucher của Duy

    @Column(name = "used_at")
    private Instant usedAt = Instant.now();
}
