package com.mybank.backend.controller;

import com.mybank.backend.entity.Account;
import com.mybank.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/account")
public class AdminAccountController {

    @Autowired
    private AccountService accountService;

    // 分页+多条件查询（加上 /page，和客户管理风格一致）
    @GetMapping("/page")
    public Page<Account> queryAccounts(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String accountType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return accountService.queryAccounts(id, customerId, accountType, page, size);
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    public Account addAccount(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return accountService.updateAccountById(id, account);
    }

    @DeleteMapping("/{id}")
    public boolean deleteAccount(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }
}