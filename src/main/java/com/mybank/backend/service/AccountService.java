package com.mybank.backend.service;

import com.mybank.backend.entity.Account;

public interface AccountService {
    Account getAccountByUsername(String username);
    Account updateAccountByUsername(String username, Account account);
    boolean verifyFace(String username, String faceImage);
    String getDecryptedAccountNumber(Long accountId);
}