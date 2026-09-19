package com.finalproject2.repository;

import com.finalproject2.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {

    List<UserAddress> findByAccountIdOrderByIsDefaultDescCreatedAtDesc(Integer accountId);

    Optional<UserAddress> findByIdAndAccountId(Integer id, Integer accountId);

    Optional<UserAddress> findByAccountIdAndIsDefaultTrue(Integer accountId);

    long countByAccountId(Integer accountId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.account.id = :accountId")
    void clearDefaultForAccount(@Param("accountId") Integer accountId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.account.id = :accountId AND ua.id <> :exceptId")
    void clearDefaultForAccountExcept(@Param("accountId") Integer accountId, @Param("exceptId") Integer exceptId);
}
