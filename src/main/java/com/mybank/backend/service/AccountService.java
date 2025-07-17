package com.mybank.backend.service;

import com.mybank.backend.entity.Account;
import com.mybank.backend.entity.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService {
    // 分页+多条件查询
    Page<Account> queryAccounts(Long id, Long customerId, String accountType, int page, int size);

    Account getAccountByCustomerId(Long customerId);
    Account getAccountById(Long id);
    Account getAccountByUsername(String username);
    Account updateAccountByUsername(String username, Account account);
    boolean verifyFace(String username, String faceImage);
    String getDecryptedAccountNumber(Long accountId);

    boolean deposit(Long accountId, Double amount, String type, String description);
    boolean withdraw(Long accountId, Double amount, String description);
    boolean transfer(Long fromAccountId, Long toAccountId, Double amount, String description);

    List<Transaction> getTransactionHistory(Long accountId);

    String recognizeFace(String faceImage);
    List<Account> getAllAccounts();
    Account addAccount(Account account);
    boolean deleteAccount(Long accountId);
    Account updateAccountById(Long accountId, Account account);
}