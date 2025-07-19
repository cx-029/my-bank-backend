package com.mybank.backend.controller;

import com.mybank.backend.entity.WealthProductTransaction;
import com.mybank.backend.repository.WealthProductTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wealth/transaction")
public class WealthProductTransactionController {
    @Autowired
    private WealthProductTransactionRepository transactionRepository;

    @GetMapping("/position/{positionId}")
    public List<WealthProductTransaction> getTransactions(@PathVariable Long positionId) {
        return transactionRepository.findByPositionId(positionId);
    }
}