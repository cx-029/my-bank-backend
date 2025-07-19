package com.mybank.backend.repository;

import com.mybank.backend.entity.WealthProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WealthProductRepository extends JpaRepository<WealthProduct, Long>, JpaSpecificationExecutor<WealthProduct> {
}