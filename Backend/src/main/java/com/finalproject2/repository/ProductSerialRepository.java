package com.finalproject2.repository;

import com.finalproject2.entity.ProductSerial;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductSerialRepository extends JpaRepository<ProductSerial, Long> {

    @Query("SELECT ps FROM ProductSerial ps WHERE ps.variant.id = :variantId AND ps.status = 'IN_STOCK'")
    List<ProductSerial> findAvailableSerials(@Param("variantId") Long variantId, Pageable pageable);

    @Query("SELECT ps FROM ProductSerial ps WHERE ps.variant.id = :variantId AND ps.status = 'IN_STOCK'")
    List<ProductSerial> findAllAvailableSerials(@Param("variantId") Long variantId);

    List<ProductSerial> findByOrderItemId(Long orderItemId);
    
    List<ProductSerial> findBySerialCodeIn(List<String> serialCodes);
}
