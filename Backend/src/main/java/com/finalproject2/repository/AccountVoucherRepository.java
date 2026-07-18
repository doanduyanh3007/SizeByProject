package com.finalproject2.repository;

import com.finalproject2.entity.AccountVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountVoucherRepository extends JpaRepository<AccountVoucher, Integer> {
    List<AccountVoucher> findAllByAccountId(Integer accountId);
    boolean existsByAccountIdAndVoucherId(Integer accountId, Long voucherId);

    void deleteByAccountIdAndVoucherId(Integer id, Long id1);
}
