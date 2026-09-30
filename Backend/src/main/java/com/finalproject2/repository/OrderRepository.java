package com.finalproject2.repository;

import com.finalproject2.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAccountId(Integer accountId);
    int countByPhoneAndVoucherId(String phone, Long voucherId);
    int countByAccountIdAndVoucherId(Integer accountId, Long voucherId);

    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.paymentStatus = 'PENDING' AND (o.paymentMethod.id = 2 OR LOWER(o.paymentMethod.name) LIKE '%vnpay%') AND o.createdAt < :cutoff")
    List<Order> findPendingOnlineOrders(@org.springframework.data.repository.query.Param("cutoff") java.time.Instant cutoff);
}