package com.mybank.backend.service;

import com.mybank.backend.entity.Account;
import com.mybank.backend.entity.Transaction;

import java.util.List;

public interface AccountService {
    Account getAccountByCustomerId(Long customerId);
    Account getAccountByUsername(String username);
    Account updateAccountByUsername(String username, Account account);
    boolean verifyFace(String username, String faceImage);
    String getDecryptedAccountNumber(Long accountId);

    // 新增账户资金操作和流水查询
    boolean deposit(Long accountId, Double amount, String type, String description);
    boolean withdraw(Long accountId, Double amount, String description);
    boolean transfer(Long fromAccountId, Long toAccountId, Double amount, String description);

    List<Transaction> getTransactionHistory(Long accountId);

    // 新增：直接用人脸图片识别用户（返回用户名或userId）
    String recognizeFace(String faceImage);
    List<Account> getAllAccounts();
    // 新增
    Account addAccount(Account account);
    // 删除
    boolean deleteAccount(Long accountId);
    // 按账户id修改
    Account updateAccountById(Long accountId, Account account);
}