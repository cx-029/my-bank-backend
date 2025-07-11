package com.mybank.backend.controller;

import com.mybank.backend.entity.Customer;
import com.mybank.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private CustomerService customerService;

    /**
     * 获取当前用户的个人信息
     * @param principal Spring Security自带的登录用户信息
     */
    @GetMapping
    public Customer getProfile(Principal principal) {
        String realName = principal.getName();
        Optional<Customer> customer = customerService.getCustomerByName(realName);
        return customer.orElse(null);
    }

    /**
     * 修改当前用户的个人信息
     * @param principal Spring Security自带的登录用户信息
     * @param customer 前端传来的修改后的customer对象（建议前端传所有字段，姓名不可修改）
     */
    @PutMapping
    public Customer updateProfile(Principal principal, @RequestBody Customer customer) {
        String realName = principal.getName();
        Optional<Customer> dbCustomer = customerService.getCustomerByName(realName);

        if (dbCustomer.isPresent()) {
            Customer origin = dbCustomer.get();
            // 姓名不可修改，只能修改其它字段
            customer.setId(origin.getId());
            customer.setName(realName);
            return customerService.saveCustomer(customer);
        }
        return null;
    }
}