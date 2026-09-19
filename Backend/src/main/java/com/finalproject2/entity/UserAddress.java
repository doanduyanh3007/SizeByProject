package com.finalproject2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "UserAddresses")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Nationalized
    @Column(name = "label", length = 100)
    private String label;

    @Nationalized
    @Column(name = "fullname")
    private String fullname;

    @Column(name = "phone", length = 50)
    private String phone;

    @Nationalized
    @Column(name = "street_address", length = 500)
    private String streetAddress;

    @Nationalized
    @Column(name = "ward")
    private String ward;

    @Nationalized
    @Column(name = "district")
    private String district;

    @Nationalized
    @Column(name = "province")
    private String province;

    @Nationalized
    @Lob
    @Column(name = "full_address", nullable = false)
    private String fullAddress;

    @ColumnDefault("0")
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @ColumnDefault("SYSDATETIMEOFFSET()")
    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (isDefault == null) {
            isDefault = false;
        }
    }
}
