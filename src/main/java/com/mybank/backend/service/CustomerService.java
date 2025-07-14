package com.mybank.backend.service;

import com.mybank.backend.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    static String encryptIdNumber(String plain) {
        // TODO: 改成AES加密，演示用Base64
        return java.util.Base64.getEncoder().encodeToString(plain.getBytes());
    }
    static String decryptIdNumber(String cipher) {
        if (cipher == null) return "";
        try {
            return new String(java.util.Base64.getDecoder().decode(cipher));
        } catch (Exception e) {
            return "";
        }
    }

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    Optional<Customer> getCustomerByName(String name);

    Customer saveCustomer(Customer customer);

    void deleteCustomer(Long id);
}