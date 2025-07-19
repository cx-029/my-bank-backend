package com.mybank.backend.controller;

import com.mybank.backend.entity.CustomerWealthPosition;
import com.mybank.backend.service.CustomerWealthPositionService;
import com.mybank.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wealth/position")
public class CustomerWealthPositionController {
    @Autowired
    private CustomerWealthPositionService positionService;
    @Autowired
    private CustomerService customerService;

    // 申购
    @PostMapping("/purchase")
    public CustomerWealthPosition purchase(@RequestParam Long customerId,
                                           @RequestParam Long productId,
                                           @RequestParam Double amount) {
        return positionService.purchase(customerId, productId, amount);
    }

    // 赎回
    @PostMapping("/redeem")
    public CustomerWealthPosition redeem(@RequestParam Long positionId,
                                         @RequestParam Double amount) {
        return positionService.redeem(positionId, amount);
    }

    // 查询当前登录客户所有持仓（不允许传customerId）
    @GetMapping("/my")
    public List<CustomerWealthPosition> myPositions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long customerId = customerService.findCustomerIdByUsername(username);
        return positionService.getPositions(customerId);
    }
}