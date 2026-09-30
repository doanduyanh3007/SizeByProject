package com.finalproject2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.ColumnDefault;

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

    @Nationalized
    @Size(max = 255)
    @Column(name = "name", columnDefinition = "nvarchar(255)")
    private String name;

    @Size(max = 7)
    @Column(name = "hex_code", length = 7)
    private String hexCode;

    @Size(max = 255)
    @Column(name = "hexCode")
    private String hexCode1;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @JsonIgnore
    @OneToMany(mappedBy = "color")
    private Set<ProductVariant> productVariants = new LinkedHashSet<>();
}