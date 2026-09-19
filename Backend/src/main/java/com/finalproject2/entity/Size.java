package com.finalproject2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Sizes")
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Nationalized
    @jakarta.validation.constraints.Size(max = 255)
    @Column(name = "name", columnDefinition = "nvarchar(255)")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "size")
    private Set<ProductVariant> productVariants = new LinkedHashSet<>();
}