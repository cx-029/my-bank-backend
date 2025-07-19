package com.mybank.backend.controller;

import com.mybank.backend.entity.WealthProduct;
import com.mybank.backend.service.WealthProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wealth/product")
public class WealthProductController {
    @Autowired
    private WealthProductService wealthProductService;

    @GetMapping("/list")
    public List<WealthProduct> list() {
        return wealthProductService.getAllProducts();
    }

    @GetMapping("/{id}")
    public WealthProduct detail(@PathVariable Long id) {
        return wealthProductService.getProduct(id);
    }

    @PostMapping("/add")
    public WealthProduct add(@RequestBody WealthProduct product) {
        return wealthProductService.addProduct(product);
    }

    @PutMapping("/{id}")
    public WealthProduct update(@PathVariable Long id, @RequestBody WealthProduct product) {
        return wealthProductService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        wealthProductService.deleteProduct(id);
    }
}