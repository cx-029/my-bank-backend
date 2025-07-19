package com.mybank.backend.service.impl;

import com.mybank.backend.entity.WealthProduct;
import com.mybank.backend.repository.WealthProductRepository;
import com.mybank.backend.service.WealthProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class WealthProductServiceImpl implements WealthProductService {
    @Autowired
    private WealthProductRepository wealthProductRepository;

    @Override
    public List<WealthProduct> getAllProducts() {
        return wealthProductRepository.findAll();
    }

    @Override
    public WealthProduct getProduct(Long id) {
        Optional<WealthProduct> product = wealthProductRepository.findById(id);
        return product.orElse(null);
    }

    @Override
    public WealthProduct addProduct(WealthProduct product) {
        product.setId(null); // 防止误用更新
        return wealthProductRepository.save(product);
    }

    @Override
    public WealthProduct updateProduct(Long id, WealthProduct product) {
        product.setId(id);
        return wealthProductRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        wealthProductRepository.deleteById(id);
    }
}