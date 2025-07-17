package com.mybank.backend.service;

import com.mybank.backend.entity.AccountLossReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountLossReportService {
    boolean applyLoss(Long accountId, String type, String reason);
    boolean releaseLoss(Long accountId);
    List<AccountLossReport> getLossReports(Long accountId);

    Page<AccountLossReport> findAll(Pageable pageable);
    AccountLossReport findById(Long id);
    AccountLossReport save(AccountLossReport report);
    void deleteById(Long id);

    // 用于controller条件分页查询
    Page<AccountLossReport> findByAccountIdAndStatus(Long accountId, String status, Pageable pageable);
    Page<AccountLossReport> findByAccountId(Long accountId, Pageable pageable);
    Page<AccountLossReport> findByStatus(String status, Pageable pageable);

    // 查询某账户最新一次挂失记录
    AccountLossReport findLatestByAccountId(Long accountId);
}