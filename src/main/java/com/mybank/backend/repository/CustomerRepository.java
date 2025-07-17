package com.mybank.backend.repository;

import com.mybank.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByName(String name);
    Optional<Customer> findByUserId(Long userId);

    // 分页模糊姓名
    Page<Customer> findByNameContaining(String name, Pageable pageable);
}