package com.mybank.backend.repository;

import com.mybank.backend.entity.WealthProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WealthProductRepository extends JpaRepository<WealthProduct, Long> {
}