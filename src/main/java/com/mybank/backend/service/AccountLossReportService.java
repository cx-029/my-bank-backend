package com.mybank.backend.service;

import com.mybank.backend.entity.AccountLossReport;
import java.util.List;

public interface AccountLossReportService {
    boolean applyLoss(Long accountId, String type, String reason);
    boolean releaseLoss(Long accountId);
    List<AccountLossReport> getLossReports(Long accountId);
}