package com.mybank.backend.controller;

import com.mybank.backend.entity.Customer;
import com.mybank.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/customer")
public class AdminCustomerController {
    @Autowired
    private CustomerService customerService;

    // 分页+模糊/精确查询
    @GetMapping("/page")
    public Page<Customer> getCustomersPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long id
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        if (name != null && !name.isEmpty()) {
            return customerService.findByNameLike(name, pageable);
        } else if (id != null) {
            return customerService.findByIdPaged(id, pageable);
        }
        return customerService.getAllCustomersPaged(pageable);
    }

    // 新增
    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    // 编辑
    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        return customerService.saveCustomer(customer);
    }

    // 删除
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}