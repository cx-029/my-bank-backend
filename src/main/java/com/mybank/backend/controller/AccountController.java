package com.mybank.backend.controller;

import com.mybank.backend.entity.Account;
import com.mybank.backend.service.AccountService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 查看自己的账户基本信息
    @GetMapping
    public Account getAccountInfo(Principal principal) {
        String username = principal.getName();
        return accountService.getAccountByUsername(username);
    }

    // 修改账户基本信息
    @PutMapping
    public Account updateAccount(Principal principal, @RequestBody Account account) {
        String username = principal.getName();
        return accountService.updateAccountByUsername(username, account);
    }

    // 人脸识别后查看敏感信息：银行卡号
    @PostMapping("/reveal-account-number")
    public String revealAccountNumber(@RequestBody Map<String, Object> payload, Principal principal) {
        String username = principal.getName();
        Long accountId = Long.valueOf(payload.get("accountId").toString());
        String faceImage = payload.get("faceImage").toString();

        boolean verified = accountService.verifyFace(username, faceImage);
        if (verified) {
            return accountService.getDecryptedAccountNumber(accountId);
        }
        throw new RuntimeException("人脸识别失败");
    }
}