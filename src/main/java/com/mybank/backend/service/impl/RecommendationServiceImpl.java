package com.mybank.backend.service.impl;

import com.mybank.backend.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Override
    public Map<String, Object> getRecommendations(Long accountId) {
        // 模拟从微服务获取分析数据
        Map<String, Object> financialData = fetchFinancialAnalysis(accountId);

        // 计算各项评分
        Map<String, Double> scores = calculateScores(financialData);

        // 模拟推荐结果（后续可替换为机器学习算法）
        List<String> recommendations = generateRecommendations(scores);

        // 返回评分和推荐结果
        Map<String, Object> response = new HashMap<>();
        response.put("scores", scores);
        response.put("recommendations", recommendations);
        return response;
    }

    private Map<String, Object> fetchFinancialAnalysis(Long accountId) {
        // 模拟返回的财务分析数据
        return Map.of(
                "incomeStability", 25.0,
                "expenseStability", 45.0,
                "averageCashFlow", 9258.0375,
                "liquidityMonths", 6,
                "investmentContribution", 26.257541577746867,
                "incomeTrend", List.of(-0.4, -73.49397590361446, 735.8227272727273),
                "expenseTrend", List.of(-7.03125, 100.0, 0.0)
        );
    }

    private Map<String, Double> calculateScores(Map<String, Object> financialData) {
        Map<String, Double> scores = new HashMap<>();

        // 收入稳定性评分（数值越低越好，越低分数越高）
        double incomeStability = (double) financialData.get("incomeStability");
        double incomeStabilityScore = reverseNormalizeScore(incomeStability, 0, 100); // 反转得分，越低越好
        scores.put("incomeStabilityScore", incomeStabilityScore);

        // 支出稳定性评分（数值越低越好，越低分数越高）
        double expenseStability = (double) financialData.get("expenseStability");
        double expenseStabilityScore = reverseNormalizeScore(expenseStability, 0, 100); // 越低越好
        scores.put("expenseStabilityScore", expenseStabilityScore);

        // 平均现金流评分（数值越高越好）
        double averageCashFlow = (double) financialData.get("averageCashFlow");
        double cashFlowScore = normalizeScore(averageCashFlow, 5000, 20000); // 假设合理范围为 5000-20000
        scores.put("cashFlowScore", cashFlowScore);

        // 流动性评分（月数，越多越好）
        int liquidityMonths = (int) financialData.get("liquidityMonths");
        double liquidityScore = normalizeScore(liquidityMonths, 0, 12); // 假设合理范围为 0-12 个月
        scores.put("liquidityScore", liquidityScore);

        // 投资贡献评分（20-60 为合理范围）
        double investmentContribution = (double) financialData.get("investmentContribution");
        double investmentContributionScore = normalizeScore(investmentContribution, 20, 60); // 20-60 为最佳范围
        scores.put("investmentContributionScore", investmentContributionScore);

        // 收入趋势评分（正值表示增长，负值表示下降）
        List<Double> incomeTrend = (List<Double>) financialData.get("incomeTrend");
        double incomeTrendScore = calculateTrendImpact(incomeTrend, true); // 趋势为正得分高
        scores.put("incomeTrendScore", normalizeScore(incomeTrendScore, -100, 100)); // 范围为 -100 到 100

        // 支出趋势评分（负值表示下降，正值表示增加，下降趋势得分高）
        List<Double> expenseTrend = (List<Double>) financialData.get("expenseTrend");
        double expenseTrendScore = calculateTrendImpact(expenseTrend, false); // 趋势为负得分高
        scores.put("expenseTrendScore", normalizeScore(expenseTrendScore, -100, 100)); // 范围为 -100 到 100

        return scores;
    }

    /**
     * 反转归一化评分（数值越低越好）
     *
     * @param value 当前值
     * @param min   最小值
     * @param max   最大值
     * @return 归一化后的评分（0-100）
     */
    private double reverseNormalizeScore(double value, double min, double max) {
        return Math.max(0, Math.min(100, (max - value) / (max - min) * 100));
    }

    /**
     * 归一化评分（数值越高越好）
     *
     * @param value 当前值
     * @param min   最小值
     * @param max   最大值
     * @return 归一化后的评分（0-100）
     */
    private double normalizeScore(double value, double min, double max) {
        return Math.max(0, Math.min(100, (value - min) / (max - min) * 100));
    }

    /**
     * 计算趋势影响（收入或支出趋势）
     *
     * @param trend     趋势数据
     * @param isPositive 趋势是否为正向
     * @return 趋势分数影响值
     */
    private double calculateTrendImpact(List<Double> trend, boolean isPositive) {
        double sum = trend.stream().mapToDouble(Double::doubleValue).sum();
        return isPositive ? sum : -sum; // 正向趋势直接加和，负向趋势取相反数
    }

    /**
     * 根据评分生成推荐结果
     *
     * @param scores 评分数据
     * @return 推荐结果
     */
    private List<String> generateRecommendations(Map<String, Double> scores) {
        double cashFlowScore = scores.get("cashFlowScore");
        double investmentScore = scores.get("investmentContributionScore");

        if (cashFlowScore > 80 && investmentScore > 50) {
            return List.of("高收益理财产品", "长期债券", "指数型基金");
        } else if (cashFlowScore > 50) {
            return List.of("稳健型基金", "储蓄型保险");
        } else {
            return List.of("短期储蓄", "流动性增强工具");
        }
    }
}