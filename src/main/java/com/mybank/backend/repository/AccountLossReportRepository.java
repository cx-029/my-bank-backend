package com.mybank.backend.repository;

import com.mybank.backend.entity.AccountLossReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountLossReportRepository extends JpaRepository<AccountLossReport, Long> {
    List<AccountLossReport> findByAccountId(Long accountId);

    Optional<AccountLossReport> findByAccountIdAndStatus(Long accountId, String status);

    Page<AccountLossReport> findByAccountIdAndStatus(Long accountId, String status, Pageable pageable);

    Page<AccountLossReport> findByAccountId(Long accountId, Pageable pageable);

    Page<AccountLossReport> findByStatus(String status, Pageable pageable);

    List<AccountLossReport> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    // 统计所有账户的最新一条挂失记录，且状态为“待处理”的数量
    @Query("SELECT COUNT(r) FROM AccountLossReport r " +
            "WHERE r.id IN (SELECT MAX(r2.id) FROM AccountLossReport r2 GROUP BY r2.accountId) " +
            "AND r.status = :status")
    long countLatestByStatus(@Param("status") String status);
}