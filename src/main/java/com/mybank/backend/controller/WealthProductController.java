package com.mybank.backend.controller;

import com.mybank.backend.entity.WealthProduct;
import com.mybank.backend.service.WealthProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wealth/product") // 关键：加上/api前缀
public class WealthProductController {
    @Autowired
    private WealthProductService wealthProductService;

    // 分页+条件查询接口
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // page为1时PageRequest实际是0
        Page<WealthProduct> pageResult = wealthProductService.pageQuery(name, type, riskLevel, page, size);
        Map<String, Object> res = new HashMap<>();
        res.put("content", pageResult.getContent());
        res.put("totalElements", pageResult.getTotalElements());
        return res;
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