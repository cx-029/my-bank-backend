package com.mybank.backend.service.impl;

import com.mybank.backend.entity.Account;
import com.mybank.backend.entity.AccountLossReport;
import com.mybank.backend.repository.AccountLossReportRepository;
import com.mybank.backend.repository.AccountRepository;
import com.mybank.backend.service.AccountLossReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (lossReportRepository.findByAccountIdAndStatus(accountId, "APPLIED").isPresent()) {
            return false;
        }
        // 新增挂失记录
        AccountLossReport report = new AccountLossReport();
        report.setAccountId(accountId);
        report.setType(type);
        report.setReason(reason);
        report.setStatus("APPLIED");
        report.setCreatedAt(LocalDateTime.now());
        lossReportRepository.save(report);

        // 冻结账户
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
        var optional = lossReportRepository.findByAccountIdAndStatus(accountId, "APPLIED");
        if (optional.isEmpty()) return false;
        AccountLossReport report = optional.get();
        report.setStatus("RELEASED");
        report.setResolvedAt(LocalDateTime.now());
        lossReportRepository.save(report);

        // 恢复账户
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
}