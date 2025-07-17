package com.mybank.backend.repository;

import com.mybank.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByAccountIdOrderByTransactionTimeDesc(Long accountId);

    // 支持分页
    Page<Transaction> findByAccountIdOrderByTransactionTimeDesc(Long accountId, Pageable pageable);

    // 按类型筛选
    Page<Transaction> findByTypeOrderByTransactionTimeDesc(String type, Pageable pageable);

    // 按账户和类型筛选
    Page<Transaction> findByAccountIdAndTypeOrderByTransactionTimeDesc(Long accountId, String type, Pageable pageable);
}