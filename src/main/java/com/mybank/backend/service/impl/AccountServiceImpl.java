package com.mybank.backend.service.impl;

import com.mybank.backend.entity.Account;
import com.mybank.backend.entity.Customer;
import com.mybank.backend.entity.Transaction;
import com.mybank.backend.repository.AccountRepository;
import com.mybank.backend.repository.CustomerRepository;
import com.mybank.backend.repository.TransactionRepository;
import com.mybank.backend.service.AccountService;
import com.mybank.backend.service.FaceRecognitionService;
import com.mybank.backend.util.IdNumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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

    // 存款
    @Override
    @Transactional
    public boolean deposit(Long accountId, Double amount, String type, String description) {
        Account acc = accountRepository.findById(accountId).orElse(null);
        if (acc == null || !"正常".equals(acc.getStatus())) return false;
        acc.setBalance(acc.getBalance() + amount);
        accountRepository.save(acc);

        Transaction txn = new Transaction();
        txn.setAccountId(accountId);
        txn.setType(type); // 活期/定期存款
        txn.setAmount(amount);
        txn.setBalanceAfter(acc.getBalance());
        txn.setDescription(description);
        txn.setTransactionTime(LocalDateTime.now());
        transactionRepository.save(txn);
        return true;
    }

    // 取款
    @Override
    @Transactional
    public boolean withdraw(Long accountId, Double amount, String description) {
        Account acc = accountRepository.findById(accountId).orElse(null);
        if (acc == null || !"正常".equals(acc.getStatus())) return false;
        if (acc.getBalance() < amount) return false;
        acc.setBalance(acc.getBalance() - amount);
        accountRepository.save(acc);

        Transaction txn = new Transaction();
        txn.setAccountId(accountId);
        txn.setType("取款");
        txn.setAmount(amount);
        txn.setBalanceAfter(acc.getBalance());
        txn.setDescription(description);
        txn.setTransactionTime(LocalDateTime.now());
        transactionRepository.save(txn);
        return true;
    }

    // 转账
    @Override
    @Transactional
    public boolean transfer(Long fromAccountId, Long toAccountId, Double amount, String description) {
        Account from = accountRepository.findById(fromAccountId).orElse(null);
        Account to = accountRepository.findById(toAccountId).orElse(null);
        if (from == null || to == null) return false;
        if (!"正常".equals(from.getStatus()) || !"正常".equals(to.getStatus())) return false;
        if (from.getBalance() < amount) return false;
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction outTxn = new Transaction();
        outTxn.setAccountId(fromAccountId);
        outTxn.setType("转出");
        outTxn.setAmount(amount);
        outTxn.setBalanceAfter(from.getBalance());
        outTxn.setDescription(description);
        outTxn.setTransactionTime(LocalDateTime.now());
        transactionRepository.save(outTxn);

        Transaction inTxn = new Transaction();
        inTxn.setAccountId(toAccountId);
        inTxn.setType("转入");
        inTxn.setAmount(amount);
        inTxn.setBalanceAfter(to.getBalance());
        inTxn.setDescription(description);
        inTxn.setTransactionTime(LocalDateTime.now());
        transactionRepository.save(inTxn);

        return true;
    }

    // 查询交易记录
    @Override
    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionTimeDesc(accountId);
    }

    // 新增：直接用人脸图片识别用户（返回用户名或userId）
    @Override
    public String recognizeFace(String faceImage) {
        return faceRecognitionService.recognize(faceImage);
    }
}