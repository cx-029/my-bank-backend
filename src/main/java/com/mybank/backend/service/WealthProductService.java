package com.mybank.backend.service;

import com.mybank.backend.entity.WealthProduct;
import java.util.List;

public interface WealthProductService {
    List<WealthProduct> getAllProducts();
    WealthProduct getProduct(Long id);
    WealthProduct addProduct(WealthProduct product);
    WealthProduct updateProduct(Long id, WealthProduct product);
    void deleteProduct(Long id);
}