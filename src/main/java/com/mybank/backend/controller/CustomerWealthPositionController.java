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

    // 申购接口：只接收productId和amount，customerId从后端获取
    @PostMapping("/purchase")
    public CustomerWealthPosition purchase(@RequestBody CustomerWealthPosition position) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long customerId = customerService.findCustomerIdByUsername(username);
        return positionService.purchase(customerId, position.getProductId(), position.getAmount());
    }

    // 赎回
    @PostMapping("/redeem")
    public CustomerWealthPosition redeem(@RequestBody CustomerWealthPosition position) {
        return positionService.redeem(position.getId(), position.getAmount());
    }

    // 查询当前登录客户所有持仓
    @GetMapping("/my")
    public List<CustomerWealthPosition> myPositions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long customerId = customerService.findCustomerIdByUsername(username);
        return positionService.getPositions(customerId);
    }
}