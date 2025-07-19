package com.mybank.backend.repository;

import com.mybank.backend.entity.CustomerWealthPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerWealthPositionRepository extends JpaRepository<CustomerWealthPosition, Long> {
    List<CustomerWealthPosition> findByCustomerId(Long customerId);
    List<CustomerWealthPosition> findByCustomerIdAndStatus(Long customerId, String status);
}