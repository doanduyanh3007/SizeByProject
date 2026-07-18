package com.finalproject2.repository;

import com.finalproject2.entity.ProductCompare;
import com.finalproject2.entity.ProductCompareId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCompareRepository extends JpaRepository<ProductCompare, ProductCompareId> {
}