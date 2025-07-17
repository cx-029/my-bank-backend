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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // 分页+多条件查询
    @Override
    public Page<Account> queryAccounts(Long id, Long customerId, String accountType, int page, int size) {
        Specification<Account> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customerId"), customerId));
            }
            if (accountType != null && !accountType.isEmpty()) {
                predicates.add(cb.equal(root.get("accountType"), accountType));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        // 修正分页参数，确保不会小于0
        int pageIndex = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "id"));
        return accountRepository.findAll(spec, pageable);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account addAccount(Account account) {
        if (account.getAccountNumber() != null && !account.getAccountNumber().isEmpty()) {
            account.setEncryptedAccountNumber(IdNumberUtil.encryptIdNumber(account.getAccountNumber()));
        }
        if (account.getOpenDate() == null) {
            account.setOpenDate(java.time.LocalDate.now());
        }
        return accountRepository.save(account);
    }

    @Override
    public boolean deleteAccount(Long accountId) {
        if (accountRepository.existsById(accountId)) {
            accountRepository.deleteById(accountId);
            return true;
        }
        return false;
    }

    @Override
    public Account updateAccountById(Long accountId, Account account) {
        Account acc = accountRepository.findById(accountId).orElse(null);
        if (acc != null) {
            acc.setAccountType(account.getAccountType());
            acc.setStatus(account.getStatus());
            acc.setBalance(account.getBalance());
            if (account.getAccountNumber() != null && !account.getAccountNumber().isEmpty()) {
                acc.setEncryptedAccountNumber(IdNumberUtil.encryptIdNumber(account.getAccountNumber()));
            }
            return accountRepository.save(acc);
        }
        return null;
    }

    @Override
    public Account getAccountByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId).orElse(null);
    }

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

    @Override
    public Account updateAccountByUsername(String username, Account account) {
        Customer customer = customerRepository.findByName(username).orElse(null);
        if (customer == null) return null;
        Account acc = accountRepository.findByCustomerId(customer.getId()).orElse(null);
        if (acc != null) {
            acc.setAccountType(account.getAccountType());
            acc.setStatus(account.getStatus());
            acc.setBalance(account.getBalance());
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

    @Override
    @Transactional
    public boolean deposit(Long accountId, Double amount, String type, String description) {
        Account acc = accountRepository.findById(accountId).orElse(null);
        if (acc == null || !"正常".equals(acc.getStatus())) return false;
        acc.setBalance(acc.getBalance() + amount);
        accountRepository.save(acc);

        Transaction txn = new Transaction();
        txn.setAccountId(accountId);
        txn.setType(type);
        txn.setAmount(amount);
        txn.setBalanceAfter(acc.getBalance());
        txn.setDescription(description);
        txn.setTransactionTime(LocalDateTime.now());
        transactionRepository.save(txn);
        return true;
    }

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

    @Override
    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionTimeDesc(accountId);
    }

    @Override
    public String recognizeFace(String faceImage) {
        return faceRecognitionService.recognize(faceImage);
    }

}