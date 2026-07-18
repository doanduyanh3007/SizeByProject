package com.finalproject2.repository;

import com.finalproject2.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    @EntityGraph(attributePaths = "roles")
    Optional<Account> findByGmail(String gmail);

    boolean existsByGmail(String gmail);

    @Override
    @EntityGraph(attributePaths = "roles")
    Optional<Account> findById(Integer id);

    @Override
    @EntityGraph(attributePaths = "roles")
    List<Account> findAll();
}