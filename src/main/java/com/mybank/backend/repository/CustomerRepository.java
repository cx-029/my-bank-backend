package com.mybank.backend.repository;

import com.mybank.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // 可自定义扩展方法
}