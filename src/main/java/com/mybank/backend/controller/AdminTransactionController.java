package com.mybank.backend.controller;

import com.mybank.backend.entity.Transaction;
import com.mybank.backend.entity.Account;
import com.mybank.backend.service.AccountService;
import com.mybank.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/transactions")
public class AdminTransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    // 分页+类型筛选查询所有交易
    @GetMapping
    public Page<Transaction> getAllTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String type
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "transactionTime"));
        if (accountId != null && type != null && !type.isEmpty()) {
            return transactionRepository.findByAccountIdAndTypeOrderByTransactionTimeDesc(accountId, type, pageable);
        } else if (accountId != null) {
            return transactionRepository.findByAccountIdOrderByTransactionTimeDesc(accountId, pageable);
        } else if (type != null && !type.isEmpty()) {
            return transactionRepository.findByTypeOrderByTransactionTimeDesc(type, pageable);
        } else {
            return transactionRepository.findAll(pageable);
        }
    }

    // 新增一条存款
    @PostMapping("/deposit")
    public boolean adminDeposit(@RequestBody Transaction tx) {
        Account account = accountService.getAccountById(tx.getAccountId());
        return accountService.deposit(account.getId(), tx.getAmount(), "管理员存款", tx.getDescription());
    }

    // 新增一条取款
    @PostMapping("/withdraw")
    public boolean adminWithdraw(@RequestBody Transaction tx) {
        Account account = accountService.getAccountById(tx.getAccountId());
        return accountService.withdraw(account.getId(), tx.getAmount(), tx.getDescription());
    }

    // 新增转账
    @PostMapping("/transfer")
    public boolean adminTransfer(@RequestBody Transaction tx, @RequestParam Long toAccountId) {
        return accountService.transfer(tx.getAccountId(), toAccountId, tx.getAmount(), tx.getDescription());
    }

    // 删除一条交易记录
    @DeleteMapping("/{id}")
    public boolean deleteTransaction(@PathVariable Long id) {
        transactionRepository.deleteById(id);
        return true;
    }

    // 修改一条交易描述（通常不允许改金额/账户）
    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction tx) {
        Transaction oldTx = transactionRepository.findById(id).orElse(null);
        if (oldTx != null) {
            oldTx.setDescription(tx.getDescription());
            return transactionRepository.save(oldTx);
        }
        return null;
    }

    // 查询单条
    @GetMapping("/{id}")
    public Transaction getTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id).orElse(null);
    }
}