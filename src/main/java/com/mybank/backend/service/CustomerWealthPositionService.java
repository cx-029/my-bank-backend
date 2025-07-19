package com.mybank.backend.service;

import com.mybank.backend.entity.CustomerWealthPosition;
import java.util.List;

public interface CustomerWealthPositionService {
    CustomerWealthPosition purchase(Long customerId, Long productId, Double amount);
    CustomerWealthPosition redeem(Long positionId, Double amount);
    List<CustomerWealthPosition> getPositions(Long customerId);
}