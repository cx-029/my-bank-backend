package com.mybank.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_wealth_position")
public class CustomerWealthPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private Long productId;
    private Double amount;
    private Double expectedIncome;
    private String status;
    private LocalDateTime purchaseDate;
    private LocalDateTime latestUpdate;
    private String productName;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getExpectedIncome() { return expectedIncome; }
    public void setExpectedIncome(Double expectedIncome) { this.expectedIncome = expectedIncome; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDateTime getLatestUpdate() { return latestUpdate; }
    public void setLatestUpdate(LocalDateTime latestUpdate) { this.latestUpdate = latestUpdate; }
}