package com.finalproject2.repository;

import com.finalproject2.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT p.* FROM Products p JOIN (SELECT TOP 5 pv.product_id, SUM(oi.quantity) as total_qty FROM ProductVariants pv JOIN OrderItems oi ON pv.id = oi.variant_id GROUP BY pv.product_id ORDER BY total_qty DESC) top_sales ON p.id = top_sales.product_id", nativeQuery = true)
    List<Product> getTopSellingProducts();

    @Query(value = "SELECT p.* FROM Products p JOIN (SELECT TOP 5 product_id, AVG(CAST(rating AS FLOAT)) as avg_rating FROM ProductReviews GROUP BY product_id ORDER BY avg_rating DESC) top_rated ON p.id = top_rated.product_id", nativeQuery = true)
    List<Product> getTopRatedProducts();
}