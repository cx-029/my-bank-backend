package com.mybank.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AccountLossReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    private String type; // 挂失类型（口头挂失/正式挂失）

    private String reason; // 挂失原因

    private String status; // APPLIED/RELEASED

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    public AccountLossReport() {}

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "APPLIED";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}