package com.mybank.backend.repository;

import com.mybank.backend.entity.AccountLossReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AccountLossReportRepository extends JpaRepository<AccountLossReport, Long> {
    List<AccountLossReport> findByAccountId(Long accountId);

    Optional<AccountLossReport> findByAccountIdAndStatus(Long accountId, String status);

    // 分页查询挂失记录
    Page<AccountLossReport> findByAccountIdAndStatus(Long accountId, String status, Pageable pageable);

    Page<AccountLossReport> findByAccountId(Long accountId, Pageable pageable);

    Page<AccountLossReport> findByStatus(String status, Pageable pageable);

    // 查询某账户挂失记录按创建时间倒序（用于只操作最新一次挂失记录）
    List<AccountLossReport> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}