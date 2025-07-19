package com.mybank.backend.service.impl;

import com.mybank.backend.entity.CustomerWealthPosition;
import com.mybank.backend.entity.WealthProduct;
import com.mybank.backend.entity.WealthProductTransaction;
import com.mybank.backend.repository.CustomerWealthPositionRepository;
import com.mybank.backend.repository.WealthProductRepository;
import com.mybank.backend.repository.WealthProductTransactionRepository;
import com.mybank.backend.service.CustomerWealthPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerWealthPositionServiceImpl implements CustomerWealthPositionService {
    @Autowired
    private CustomerWealthPositionRepository positionRepository;
    @Autowired
    private WealthProductRepository productRepository;
    @Autowired
    private WealthProductTransactionRepository transactionRepository;

    @Override
    public CustomerWealthPosition purchase(Long customerId, Long productId, Double amount) {
        // 校验产品及申购金额
        Optional<WealthProduct> op = productRepository.findById(productId);
        if (op.isEmpty()) throw new RuntimeException("理财产品不存在");
        WealthProduct product = op.get();
        if (amount < product.getMinAmount()) throw new RuntimeException("申购金额低于最低起购额");

        // 新建持仓
        CustomerWealthPosition position = new CustomerWealthPosition();
        position.setCustomerId(customerId);
        position.setProductId(productId);
        position.setAmount(amount);
        position.setPurchaseDate(LocalDateTime.now());
        position.setStatus("持有");
        position.setLatestUpdate(LocalDateTime.now());
        position.setExpectedIncome(amount * (product.getInterestRate() != null ? product.getInterestRate() : 0));
        CustomerWealthPosition saved = positionRepository.save(position);

        // 记一条申购交易明细
        WealthProductTransaction tx = new WealthProductTransaction();
        tx.setPositionId(saved.getId());
        tx.setTradeType("申购");
        tx.setAmount(amount);
        tx.setTradeTime(LocalDateTime.now());
        transactionRepository.save(tx);

        return saved;
    }

    @Override
    public CustomerWealthPosition redeem(Long positionId, Double amount) {
        Optional<CustomerWealthPosition> op = positionRepository.findById(positionId);
        if (op.isEmpty()) throw new RuntimeException("持仓不存在");
        CustomerWealthPosition position = op.get();
        if (amount > position.getAmount()) throw new RuntimeException("赎回金额超出持仓可用额度");

        // 更新持仓
        position.setAmount(position.getAmount() - amount);
        if (position.getAmount() <= 0) {
            position.setStatus("已赎回");
        }
        position.setLatestUpdate(LocalDateTime.now());
        CustomerWealthPosition saved = positionRepository.save(position);

        // 记一条赎回交易明细
        WealthProductTransaction tx = new WealthProductTransaction();
        tx.setPositionId(positionId);
        tx.setTradeType("赎回");
        tx.setAmount(amount);
        tx.setTradeTime(LocalDateTime.now());
        transactionRepository.save(tx);

        return saved;
    }

    @Override
    public List<CustomerWealthPosition> getPositions(Long customerId) {
        return positionRepository.findByCustomerId(customerId);
    }
}