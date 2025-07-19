package com.mybank.backend.service.impl;

import com.mybank.backend.entity.WealthProduct;
import com.mybank.backend.repository.WealthProductRepository;
import com.mybank.backend.service.WealthProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.criteria.Predicate;

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

    @Override
    public Page<WealthProduct> pageQuery(String name, String type, String riskLevel, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "id"));
        return wealthProductRepository.findAll((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (name != null && !name.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("name"), "%" + name.trim() + "%"));
            }
            if (type != null && !type.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), type.trim()));
            }
            if (riskLevel != null && !riskLevel.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("riskLevel"), riskLevel.trim()));
            }
            return predicate;
        }, pageable);
    }
}