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

    Page<AccountLossReport> findByAccountIdAndStatus(Long accountId, String status, Pageable pageable);
    Page<AccountLossReport> findByAccountId(Long accountId, Pageable pageable);
    Page<AccountLossReport> findByStatus(String status, Pageable pageable);

    AccountLossReport findLatestByAccountId(Long accountId);

    long countLatestByStatus(String status);

    // 新增，批量同步某账户所有挂失记录状态
    void updateAllReportsStatusByAccount(Long accountId, String status);
}