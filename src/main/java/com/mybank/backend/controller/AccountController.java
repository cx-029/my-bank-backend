package com.mybank.backend.controller;

import com.mybank.backend.entity.Account;
import com.mybank.backend.entity.Transaction;
import com.mybank.backend.service.AccountService;
import com.mybank.backend.service.FaceRecognitionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

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

    // 存款
    @PostMapping("/deposit")
    public boolean deposit(Principal principal, @RequestBody Map<String, Object> payload) {
        String username = principal.getName();
        Account account = accountService.getAccountByUsername(username);
        if (account == null) throw new RuntimeException("未找到账户信息");
        Double amount = Double.valueOf(payload.get("amount").toString());
        String type = payload.getOrDefault("type", "活期存款").toString();
        String description = payload.getOrDefault("description", "用户存款").toString();
        return accountService.deposit(account.getId(), amount, type, description);
    }

    // 存取管理前人脸识别校验接口（直接用FaceRecognitionService）
    @PostMapping("/face-verify")
    public Map<String, Object> faceVerify(@RequestBody Map<String, String> payload) {
        String base64Image = payload.get("image");
        String recognizedUser = faceRecognitionService.recognize(base64Image);
        Map<String, Object> result = new HashMap<>();
        if (recognizedUser != null) {
            result.put("success", true);
            result.put("username", recognizedUser); // 可返回userId或username
        } else {
            result.put("success", false);
            result.put("error", "人脸识别失败");
        }
        return result;
    }

    // 取款
    @PostMapping("/withdraw")
    public boolean withdraw(Principal principal, @RequestBody Map<String, Object> payload) {
        String username = principal.getName();
        Account account = accountService.getAccountByUsername(username);
        if (account == null) throw new RuntimeException("未找到账户信息");
        Double amount = Double.valueOf(payload.get("amount").toString());
        String description = payload.getOrDefault("description", "用户取款").toString();
        return accountService.withdraw(account.getId(), amount, description);
    }

    // 转账
    @PostMapping("/transfer")
    public boolean transfer(Principal principal, @RequestBody Map<String, Object> payload) {
        String username = principal.getName();
        Account fromAccount = accountService.getAccountByUsername(username);
        if (fromAccount == null) throw new RuntimeException("未找到账户信息");
        Long toAccountId = Long.valueOf(payload.get("toAccountId").toString());
        Double amount = Double.valueOf(payload.get("amount").toString());
        String description = payload.getOrDefault("description", "账户转账").toString();
        return accountService.transfer(fromAccount.getId(), toAccountId, amount, description);
    }

    // 查询自己的交易记录
    @GetMapping("/transactions")
    public List<Transaction> getTransactions(Principal principal) {
        String username = principal.getName();
        Account account = accountService.getAccountByUsername(username);
        if (account == null) throw new RuntimeException("未找到账户信息");
        return accountService.getTransactionHistory(account.getId());
    }
}