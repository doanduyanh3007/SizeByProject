package com.finalproject2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "account_code", nullable = false)
    private String accountCode;

    @Nationalized
    @Size(max = 255)
    @Column(name = "username")
    private String username;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "gmail", nullable = false, length = 100, unique = true)
    private String gmail;

    @Size(max = 255)
    @Column(name = "phone")
    private String phone;

    @Size(max = 255)
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Nationalized
    @Lob
    @Column(name = "address")
    private String address;

    @Size(max = 1000)
    @Column(name = "img_url")
    private String imgUrl;

    @ColumnDefault("getdate()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    // @JsonIgnore để tránh circular reference
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "AccountRoles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    @JsonIgnore
    @OneToOne(mappedBy = "account")
    private Cart cart;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<Feedback> feedbacks = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<Order> orders = new LinkedHashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "ProductCompares",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<ProductReview> productReviews = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}