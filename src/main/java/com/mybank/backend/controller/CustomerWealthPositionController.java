package com.mybank.backend.controller;

import com.mybank.backend.entity.CustomerWealthPosition;
import com.mybank.backend.service.CustomerWealthPositionService;
import com.mybank.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wealth/position")
public class CustomerWealthPositionController {
    @Autowired
    private CustomerWealthPositionService positionService;
    @Autowired
    private CustomerService customerService;

    // DTO写在controller内部且为public
    public static class RedeemRequest {
        private Long id;
        private Double amount;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }

    // 申购接口
    @PostMapping("/purchase")
    public CustomerWealthPosition purchase(@RequestBody CustomerWealthPosition position) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long customerId = customerService.findCustomerIdByUsername(username);
        return positionService.purchase(customerId, position.getProductId(), position.getAmount());
    }

    // 赎回接口
    @PostMapping("/redeem")
    public Map<String, Object> redeem(@RequestBody RedeemRequest req) {
        return positionService.redeemWithProfit(req.getId(), req.getAmount());
    }

    // 查询当前登录客户所有持仓
    @GetMapping("/my")
    public List<CustomerWealthPosition> myPositions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long customerId = customerService.findCustomerIdByUsername(username);
        return positionService.getPositions(customerId);
    }
}