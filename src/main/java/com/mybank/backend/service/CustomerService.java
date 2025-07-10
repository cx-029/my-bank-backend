package com.mybank.backend.service;

import com.mybank.backend.entity.Customer;

import java.util.List;
import java.util.Optional;

/**
 * 客户服务接口，只保留方法声明，去除静态加密/解密实现（已移到工具类）。
 */
public interface CustomerService {

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    Optional<Customer> getCustomerByName(String name);

    Customer saveCustomer(Customer customer);

    void deleteCustomer(Long id);
}