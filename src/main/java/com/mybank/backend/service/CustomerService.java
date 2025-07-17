package com.mybank.backend.service;

import com.mybank.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 客户服务接口
 */
public interface CustomerService {

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    Optional<Customer> getCustomerByName(String name);

    Customer saveCustomer(Customer customer);

    Optional<Customer> getCustomerByUserId(Long userId);

    void deleteCustomer(Long id);

    // 新增分页方法
    Page<Customer> getAllCustomersPaged(Pageable pageable);

    Page<Customer> findByNameLike(String name, Pageable pageable);

    Page<Customer> findByIdPaged(Long id, Pageable pageable);
}