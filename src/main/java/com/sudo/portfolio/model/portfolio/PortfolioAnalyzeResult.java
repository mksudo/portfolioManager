package com.sudo.portfolio.model.portfolio;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PortfolioAnalyzeResult {
    private AnalyzedResults<Double> portfolioIncomes;
    private AnalyzedResults<Double> spyIncomes;
    private AnalyzedResults<Double> portfolioReturns;
    private AnalyzedResults<Double> spyReturns;
    // portfolio
    private Map<String, Double> allocatedOverallReturn;
    private double overallReturn;
    private double standardDeviation;
    private Map<Double, Double> variances;
}
