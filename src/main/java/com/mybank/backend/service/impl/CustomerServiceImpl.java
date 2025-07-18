package com.mybank.backend.service.impl;

import com.mybank.backend.entity.Customer;
import com.mybank.backend.repository.CustomerRepository;
import com.mybank.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }

    @Override
    public Optional<Customer> getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId);
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // 分页查询所有
    @Override
    public Page<Customer> getAllCustomersPaged(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    // 分页模糊查询姓名
    @Override
    public Page<Customer> findByNameLike(String name, Pageable pageable) {
        return customerRepository.findByNameContaining(name, pageable);
    }

    // 分页模糊查询手机号
    @Override
    public Page<Customer> findByPhoneLike(String phone, Pageable pageable) {
        return customerRepository.findByPhoneContaining(phone, pageable);
    }

    // 分页模糊查询姓名和手机号
    @Override
    public Page<Customer> findByNameAndPhoneLike(String name, String phone, Pageable pageable) {
        return customerRepository.findByNameContainingAndPhoneContaining(name, phone, pageable);
    }

    // 分页精确查id（一般只返回一个）
    @Override
    public Page<Customer> findByIdPaged(Long id, Pageable pageable) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            return new PageImpl<>(List.of(customerOpt.get()), pageable, 1);
        } else {
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    @Override
    public long getCustomerCount() {
        return customerRepository.count();
    }
}