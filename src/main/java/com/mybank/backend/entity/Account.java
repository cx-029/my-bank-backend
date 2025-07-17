package com.mybank.backend.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Transient;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ★添加这行
    private Long id;

    private Long customerId;
    private String accountType;
    private String encryptedAccountNumber; // 加密后的银行卡号
    private Double balance;
    private String status;
    private LocalDate openDate;

    @Transient
    private String accountNumber; // 明文银行卡号（仅用于展示与编辑，不入库）

    public Account() {}

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getEncryptedAccountNumber() { return encryptedAccountNumber; }
    public void setEncryptedAccountNumber(String encryptedAccountNumber) { this.encryptedAccountNumber = encryptedAccountNumber; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getOpenDate() { return openDate; }
    public void setOpenDate(LocalDate openDate) { this.openDate = openDate; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
}