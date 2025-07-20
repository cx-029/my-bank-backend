package com.mybank.backend.service.impl;

import com.mybank.backend.entity.*;
import com.mybank.backend.repository.*;
import com.mybank.backend.service.CustomerWealthPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CustomerWealthPositionServiceImpl implements CustomerWealthPositionService {
    @Autowired
    private CustomerWealthPositionRepository positionRepository;
    @Autowired
    private WealthProductRepository productRepository;
    @Autowired
    private WealthProductTransactionRepository wealthTxRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public CustomerWealthPosition purchase(Long customerId, Long productId, Double amount) {
        Optional<WealthProduct> op = productRepository.findById(productId);
        if (op.isEmpty()) throw new RuntimeException("理财产品不存在");
        WealthProduct product = op.get();
        if (amount < product.getMinAmount()) throw new RuntimeException("申购金额低于最低起购额");

        Account account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("未找到用户主账户"));
        if (account.getBalance() < amount) throw new RuntimeException("账户余额不足");
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction txRecord = new Transaction();
        txRecord.setAccountId(account.getId());
        txRecord.setType("申购理财");
        txRecord.setAmount(-amount);
        txRecord.setBalanceAfter(account.getBalance());
        txRecord.setDescription("申购理财产品：" + product.getName());
        txRecord.setTransactionTime(LocalDateTime.now());
        transactionRepository.save(txRecord);

        // 年化预期收益
        double expectedIncome = amount * (product.getInterestRate() != null ? product.getInterestRate() : 0);

        CustomerWealthPosition position = new CustomerWealthPosition();
        position.setCustomerId(customerId);
        position.setProductId(productId);
        position.setProductName(product.getName());
        position.setAmount(amount);
        position.setPurchaseDate(LocalDateTime.now());
        position.setStatus("持有");
        position.setLatestUpdate(LocalDateTime.now());
        position.setExpectedIncome(expectedIncome);
        CustomerWealthPosition saved = positionRepository.save(position);

        WealthProductTransaction wtx = new WealthProductTransaction();
        wtx.setPositionId(saved.getId());
        wtx.setTradeType("申购");
        wtx.setAmount(amount);
        wtx.setTradeTime(LocalDateTime.now());
        wealthTxRepository.save(wtx);

        return saved;
    }

    // 新增：用于返回封装对象（含本次收益）
    public Map<String, Object> redeemWithProfit(Long positionId, Double amount) {
        Map<String, Object> result = new HashMap<>();
        double redeemProfit = 0.0;

        Optional<CustomerWealthPosition> op = positionRepository.findById(positionId);
        if (op.isEmpty()) throw new RuntimeException("持仓不存在");
        CustomerWealthPosition position = op.get();
        if (amount > position.getAmount()) throw new RuntimeException("赎回金额超出持仓可用额度");

        LocalDateTime now = LocalDateTime.now();
        long daysHeld = ChronoUnit.DAYS.between(position.getPurchaseDate(), now);
        if (daysHeld < 1) daysHeld = 1;

        WealthProduct product = productRepository.findById(position.getProductId())
                .orElseThrow(() -> new RuntimeException("产品不存在"));
        double rate = product.getInterestRate() != null ? product.getInterestRate() : 0.0;
        redeemProfit = amount * rate * daysHeld / 365.0;

        Account account = accountRepository.findByCustomerId(position.getCustomerId())
                .orElseThrow(() -> new RuntimeException("未找到用户主账户"));
        account.setBalance(account.getBalance() + amount + redeemProfit);
        accountRepository.save(account);

        Transaction txRecord = new Transaction();
        txRecord.setAccountId(account.getId());
        txRecord.setType("赎回理财");
        txRecord.setAmount(amount + redeemProfit);
        txRecord.setBalanceAfter(account.getBalance());
        txRecord.setDescription("赎回理财产品：" + position.getProductName() + "，本金" + amount + "，收益" + redeemProfit);
        txRecord.setTransactionTime(now);
        transactionRepository.save(txRecord);

        position.setAmount(position.getAmount() - amount);
        if (position.getAmount() <= 0) {
            position.setStatus("已赎回");
            position.setExpectedIncome(0.0);
        } else {
            // 剩余预期收益按剩余本金×年化利率
            position.setExpectedIncome(position.getAmount() * rate);
        }
        position.setLatestUpdate(now);
        CustomerWealthPosition saved = positionRepository.save(position);

        WealthProductTransaction wtx = new WealthProductTransaction();
        wtx.setPositionId(positionId);
        wtx.setTradeType("赎回");
        wtx.setAmount(amount);
        wtx.setTradeTime(now);
        wealthTxRepository.save(wtx);

        result.put("position", saved);
        result.put("redeemProfit", redeemProfit);
        return result;
    }

    @Override
    public CustomerWealthPosition redeem(Long positionId, Double amount) {
        // 兼容老接口（如只返回持仓对象）
        Map<String, Object> map = redeemWithProfit(positionId, amount);
        return (CustomerWealthPosition) map.get("position");
    }

    @Override
    public List<CustomerWealthPosition> getPositions(Long customerId) {
        return positionRepository.findByCustomerId(customerId);
    }
}