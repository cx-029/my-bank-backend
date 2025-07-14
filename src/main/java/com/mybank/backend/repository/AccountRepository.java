package com.mybank.backend.repository;

import com.mybank.backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // 根据客户ID查找账户
    Optional<Account> findByCustomerId(Long customerId);

    // 可扩展：根据账户状态查找所有账户
    List<Account> findByStatus(String status);

    // 可扩展：根据账户类型查找
    List<Account> findByAccountType(String accountType);

    // 可扩展：银行卡号查找（加密后字段，实际场景需谨慎使用）
    Optional<Account> findByEncryptedAccountNumber(String encryptedAccountNumber);
}