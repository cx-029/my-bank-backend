package com.mybank.backend.service.impl;

import com.mybank.backend.entity.Account;
import com.mybank.backend.entity.AccountLossReport;
import com.mybank.backend.repository.AccountLossReportRepository;
import com.mybank.backend.repository.AccountRepository;
import com.mybank.backend.service.AccountLossReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountLossReportServiceImpl implements AccountLossReportService {

    @Autowired
    private AccountLossReportRepository lossReportRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public boolean applyLoss(Long accountId, String type, String reason) {
        // 检查当前是否有未解除挂失
        if (lossReportRepository.findByAccountIdAndStatus(accountId, "挂失").isPresent()) {
            return false;
        }
        AccountLossReport report = new AccountLossReport();
        report.setAccountId(accountId);
        report.setType(type);
        report.setReason(reason);
        report.setStatus("挂失");
        report.setCreatedAt(LocalDateTime.now());
        lossReportRepository.save(report);

        Account account = accountRepository.findById(accountId).orElse(null);
        if (account != null) {
            account.setStatus("挂失");
            accountRepository.save(account);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean releaseLoss(Long accountId) {
        var optional = lossReportRepository.findByAccountIdAndStatus(accountId, "挂失");
        if (optional.isEmpty()) return false;
        AccountLossReport report = optional.get();
        report.setStatus("正常");
        report.setResolvedAt(LocalDateTime.now());
        lossReportRepository.save(report);

        Account account = accountRepository.findById(accountId).orElse(null);
        if (account != null) {
            account.setStatus("正常");
            accountRepository.save(account);
        }
        return true;
    }

    @Override
    public List<AccountLossReport> getLossReports(Long accountId) {
        return lossReportRepository.findByAccountId(accountId);
    }

    @Override
    public Page<AccountLossReport> findAll(Pageable pageable) {
        return lossReportRepository.findAll(pageable);
    }

    @Override
    public AccountLossReport findById(Long id) {
        return lossReportRepository.findById(id).orElse(null);
    }

    @Override
    public AccountLossReport save(AccountLossReport report) {
        return lossReportRepository.save(report);
    }

    @Override
    public void deleteById(Long id) {
        lossReportRepository.deleteById(id);
    }

    @Override
    public Page<AccountLossReport> findByAccountIdAndStatus(Long accountId, String status, Pageable pageable) {
        return lossReportRepository.findByAccountIdAndStatus(accountId, status, pageable);
    }

    @Override
    public Page<AccountLossReport> findByAccountId(Long accountId, Pageable pageable) {
        return lossReportRepository.findByAccountId(accountId, pageable);
    }

    @Override
    public Page<AccountLossReport> findByStatus(String status, Pageable pageable) {
        return lossReportRepository.findByStatus(status, pageable);
    }

    @Override
    public AccountLossReport findLatestByAccountId(Long accountId) {
        List<AccountLossReport> list = lossReportRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public long countLatestByStatus(String status) {
        return lossReportRepository.countLatestByStatus(status);
    }
}