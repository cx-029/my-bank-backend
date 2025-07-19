package com.mybank.backend.repository;

import com.mybank.backend.entity.WealthProductTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WealthProductTransactionRepository extends JpaRepository<WealthProductTransaction, Long> {
    List<WealthProductTransaction> findByPositionId(Long positionId);
}