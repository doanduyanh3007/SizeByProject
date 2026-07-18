package com.finalproject2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Colors")
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 7)
    @Column(name = "hex_code", length = 7)
    private String hexCode;

    @Size(max = 255)
    @Column(name = "hexCode")
    private String hexCode1;

    @JsonIgnore
    @OneToMany(mappedBy = "color")
    private Set<ProductVariant> productVariants = new LinkedHashSet<>();
}