package com.finalproject2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Nationalized
    @Size(max = 255)
    @Column(name = "name", columnDefinition = "nvarchar(255)")
    private String name;

    @Size(max = 250)
    @Column(name = "image_url")
    private String imageUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private Set<Product> products = new LinkedHashSet<>();
}