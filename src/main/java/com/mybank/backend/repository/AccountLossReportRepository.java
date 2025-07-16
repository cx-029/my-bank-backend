package com.mybank.backend.repository;

import com.mybank.backend.entity.AccountLossReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountLossReportRepository extends JpaRepository<AccountLossReport, Long> {
    List<AccountLossReport> findByAccountId(Long accountId);

    Optional<AccountLossReport> findByAccountIdAndStatus(Long accountId, String status);
}