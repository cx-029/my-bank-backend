package com.mybank.backend.service;

import com.mybank.backend.entity.WealthProduct;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WealthProductService {
    List<WealthProduct> getAllProducts();
    WealthProduct getProduct(Long id);
    WealthProduct addProduct(WealthProduct product);
    WealthProduct updateProduct(Long id, WealthProduct product);
    void deleteProduct(Long id);

    // 新增分页+条件查询
    Page<WealthProduct> pageQuery(String name, String type, String riskLevel, int page, int size);
}