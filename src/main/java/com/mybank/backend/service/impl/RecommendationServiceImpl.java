package com.mybank.backend.service.impl;

import com.mybank.backend.entity.WealthProduct;
import com.mybank.backend.repository.WealthProductRepository;
import com.mybank.backend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private WealthProductRepository wealthProductRepository;

    @Autowired
    private RestTemplate restTemplate; // 注入 RestTemplate，用于调用微服务

    private static final String FINANCIAL_ANALYSIS_URL = "http://localhost:8081/api/analysis/summary";

    @Override
    public Map<String, Object> getRecommendations(Long accountId) {
        // 调用微服务获取用户财务分析数据
        Map<String, Object> financialData = fetchFinancialAnalysis(accountId);

        // 动态计算评分
        Map<String, Double> scores = calculateScores(financialData);

        // 基于评分智能推荐产品
        List<WealthProduct> recommendedProducts = intelligentRecommend(scores);

        // 返回评分和推荐结果
        Map<String, Object> response = new HashMap<>();
        response.put("scores", scores);
        response.put("recommendations", recommendedProducts);
        return response;
    }

    /**
     * 调用微服务获取财务分析数据
     *
     * @param accountId 用户账户 ID
     * @return 用户财务分析数据
     */
    private Map<String, Object> fetchFinancialAnalysis(Long accountId) {
        try {
            // 调用微服务接口，传递账户 ID 获取数据
            return restTemplate.getForObject(
                    FINANCIAL_ANALYSIS_URL + "?accountId=" + accountId, Map.class);
        } catch (Exception e) {
            // 处理调用失败的情况，返回默认数据或抛出异常
            throw new RuntimeException("Failed to fetch financial analysis data: " + e.getMessage(), e);
        }
    }

    /**
     * 智能推荐逻辑
     *
     * @param scores 用户评分数据
     * @return 推荐的理财产品列表
     */
    private List<WealthProduct> intelligentRecommend(Map<String, Double> scores) {
        // 动态调整权重
        Map<String, Double> weightFactors = calculateWeightFactors(scores);

        // 获取所有在售产品
        List<WealthProduct> allProducts = wealthProductRepository.findAll().stream()
                .filter(product -> "在售".equals(product.getStatus()))
                .collect(Collectors.toList());

        // 为每个产品计算匹配得分
        List<WealthProduct> rankedProducts = allProducts.stream()
                .map(product -> calculateProductMatch(product, scores, weightFactors))
                .sorted(Comparator.comparingDouble(ProductMatch::getScore).reversed())
                .map(ProductMatch::getProduct)
                .collect(Collectors.toList());

        // 返回 1 个推荐产品
        return rankedProducts.stream().limit(3).collect(Collectors.toList());
    }

    /**
     * 动态计算评分权重
     */
    private Map<String, Double> calculateWeightFactors(Map<String, Double> scores) {
        double liquidityScore = scores.getOrDefault("liquidityScore", 0.0);
        double cashFlowScore = scores.getOrDefault("cashFlowScore", 0.0);
        double investmentContributionScore = scores.getOrDefault("investmentContributionScore", 0.0);

        double liquidityWeight = liquidityScore < 50 ? 0.4 : 0.2;  // 流动性低时增加权重
        double cashFlowWeight = cashFlowScore < 30 ? 0.3 : 0.2;     // 现金流较低时增加权重
        double investmentWeight = investmentContributionScore < 20 ? 0.3 : 0.1; // 投资贡献较低时增加权重

        // 确保权重总和为 1
        double totalWeight = liquidityWeight + cashFlowWeight + investmentWeight;
        liquidityWeight /= totalWeight;
        cashFlowWeight /= totalWeight;
        investmentWeight /= totalWeight;

        // 返回权重映射
        Map<String, Double> weights = new HashMap<>();
        weights.put("liquidityWeight", liquidityWeight);
        weights.put("cashFlowWeight", cashFlowWeight);
        weights.put("investmentWeight", investmentWeight);
        return weights;
    }

    /**
     * 计算产品匹配得分
     */
    private ProductMatch calculateProductMatch(WealthProduct product, Map<String, Double> scores, Map<String, Double> weightFactors) {
        double liquidityWeight = weightFactors.get("liquidityWeight");
        double cashFlowWeight = weightFactors.get("cashFlowWeight");
        double investmentWeight = weightFactors.get("investmentWeight");

        double liquidityScore = scores.getOrDefault("liquidityScore", 0.0);
        double cashFlowScore = scores.getOrDefault("cashFlowScore", 0.0);

        // 产品流动性得分（活期类型得高分）
        double productLiquidityScore = product.getType().equals("活期") ? 100 : 50;

        // 产品现金流得分（最低投资金额与用户现金流匹配）
        double productCashFlowScore = product.getMinAmount() <= cashFlowScore * 100 ? 100 : 50;

        // 产品投资风险得分（风险等级匹配用户偏好）
        double productInvestmentScore = product.getRiskLevel().equals("低") ? 100 : 50;

        // 计算总匹配得分
        double totalScore = liquidityWeight * productLiquidityScore +
                cashFlowWeight * productCashFlowScore +
                investmentWeight * productInvestmentScore;

        return new ProductMatch(product, totalScore);
    }

    /**
     * 动态计算评分
     */
    private Map<String, Double> calculateScores(Map<String, Object> financialData) {
        Map<String, Double> scores = new HashMap<>();

        // 收入稳定性评分
        double incomeStability = (double) financialData.get("incomeStability");
        scores.put("incomeStabilityScore", getSegmentedScore(incomeStability, new double[]{0, 50, 100, 200, 300, 400, 500}, new double[]{95, 85, 70, 50, 30, 10, 5}));

        // 支出稳定性评分
        double expenseStability = (double) financialData.get("expenseStability");
        scores.put("expenseStabilityScore", getSegmentedScore(expenseStability, new double[]{0, 50, 100, 200, 300, 400, 500}, new double[]{95, 85, 70, 50, 30, 10, 5}));

        // 平均现金流评分
        double averageCashFlow = (double) financialData.get("averageCashFlow");
        scores.put("cashFlowScore", getSegmentedScore(averageCashFlow, new double[]{5000, 10000, 15000, 20000}, new double[]{60, 80, 90, 100}));

        // 流动性评分（月数）
        int liquidityMonths = (int) financialData.get("liquidityMonths");
        scores.put("liquidityScore", getSegmentedScore(liquidityMonths, new double[]{0, 3, 6, 9, 12}, new double[]{10, 50, 80, 91, 95}));

        // 投资贡献评分
        double investmentContribution = (double) financialData.get("investmentContribution");
        scores.put("investmentContributionScore", getSegmentedScore(investmentContribution, new double[]{0, 5, 10, 15, 20, 25, 30}, new double[]{5, 30, 50, 70, 85, 91, 95}));

        // 收入趋势评分
        List<Double> incomeTrend = (List<Double>) financialData.get("incomeTrend");
        double incomeTrendSum = incomeTrend.stream().mapToDouble(Double::doubleValue).sum();
        scores.put("incomeTrendScore", getSegmentedScore(incomeTrendSum, new double[]{-50, 0, 50, 100, 150}, new double[]{5, 30, 60, 81, 91}));

        // 支出趋势评分
        List<Double> expenseTrend = (List<Double>) financialData.get("expenseTrend");
        double expenseTrendSum = expenseTrend.stream().mapToDouble(Double::doubleValue).sum();
        scores.put("expenseTrendScore", getSegmentedScore(expenseTrendSum, new double[]{-50, 0, 50, 100, 150}, new double[]{5, 30, 60, 81, 91}));

        return scores;
    }

    /**
     * 根据分段范围获取评分
     *
     * @param value 输入值
     * @param ranges 分段范围数组（升序排列）
     * @param scores 对应分段的评分数组
     * @return 输入值对应的评分
     */
    private double getSegmentedScore(double value, double[] ranges, double[] scores) {
        for (int i = 0; i < ranges.length; i++) {
            if (value <= ranges[i]) {
                return scores[i];
            }
        }
        return scores[scores.length - 1]; // 超出范围，返回最后一个评分
    }

    /**
     * 内部类：封装产品匹配得分
     */
    private static class ProductMatch {
        private final WealthProduct product;
        private final double score;

        public ProductMatch(WealthProduct product, double score) {
            this.product = product;
            this.score = score;
        }

        public WealthProduct getProduct() {
            return product;
        }

        public double getScore() {
            return score;
        }
    }
}