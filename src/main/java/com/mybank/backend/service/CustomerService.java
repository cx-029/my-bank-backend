package com.mybank.backend.service;

import com.mybank.backend.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    Customer saveCustomer(Customer customer);

    void deleteCustomer(Long id);
}