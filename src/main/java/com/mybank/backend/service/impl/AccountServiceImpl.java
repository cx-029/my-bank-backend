package com.mybank.backend.service.impl;

import com.mybank.backend.entity.Account;
import com.mybank.backend.entity.Customer;
import com.mybank.backend.repository.AccountRepository;
import com.mybank.backend.repository.CustomerRepository;
import com.mybank.backend.service.AccountService;
import com.mybank.backend.service.FaceRecognitionService;
import com.mybank.backend.util.IdNumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    // 按用户名查找账户，解密后赋值明文银行卡号
    @Override
    public Account getAccountByUsername(String username) {
        Customer customer = customerRepository.findByName(username).orElse(null);
        if (customer == null) return null;
        Account acc = accountRepository.findByCustomerId(customer.getId()).orElse(null);
        if (acc != null && acc.getEncryptedAccountNumber() != null) {
            acc.setAccountNumber(IdNumberUtil.decryptIdNumber(acc.getEncryptedAccountNumber()));
        }
        return acc;
    }

    // 按用户名修改账户（银行卡号加密后入库）
    @Override
    public Account updateAccountByUsername(String username, Account account) {
        Customer customer = customerRepository.findByName(username).orElse(null);
        if (customer == null) return null;
        Account acc = accountRepository.findByCustomerId(customer.getId()).orElse(null);
        if (acc != null) {
            acc.setAccountType(account.getAccountType());
            acc.setStatus(account.getStatus());
            acc.setBalance(account.getBalance());
            // 前端明文卡号传在 accountNumber 字段
            if (account.getAccountNumber() != null && !account.getAccountNumber().isEmpty()) {
                acc.setEncryptedAccountNumber(IdNumberUtil.encryptIdNumber(account.getAccountNumber()));
            }
            return accountRepository.save(acc);
        }
        return null;
    }

    @Override
    public boolean verifyFace(String username, String faceImage) {
        String userId = faceRecognitionService.recognize(faceImage);
        return userId != null && userId.equals(username);
    }

    @Override
    public String getDecryptedAccountNumber(Long accountId) {
        Account acc = accountRepository.findById(accountId).orElse(null);
        if (acc != null) {
            return IdNumberUtil.decryptIdNumber(acc.getEncryptedAccountNumber());
        }
        return "";
    }
}