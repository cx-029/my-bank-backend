package com.mybank.backend.service;

import java.util.Map;

public interface AnalysisService {
    /**
     * 获取账户的交易摘要
     * @param accountId 账户 ID
     * @return 交易摘要数据，包括总收入、总支出和余额
     */
    Map<String, Object> getTransactionSummary(Long accountId);
}