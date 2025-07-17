package com.mybank.backend.repository;

import com.mybank.backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Optional<Account> findById(Long id);
    Optional<Account> findByCustomerId(Long customerId);
    List<Account> findByAccountType(String accountType);
    List<Account> findByStatus(String status);
    Optional<Account> findByEncryptedAccountNumber(String encryptedAccountNumber);
    List<Account> findAll();
    void deleteById(Long id);
}