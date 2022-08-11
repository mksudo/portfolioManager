package com.sudo.portfolio.service;

import com.sudo.portfolio.model.portfolio.AnalyzedResults;
import com.sudo.portfolio.model.portfolio.Portfolio;
import com.sudo.portfolio.model.portfolio.PortfolioAnalyzeResult;
import com.sudo.portfolio.model.portfolio.TimedResult;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This class analyzes provided portfolios and compute
 * corresponding incomes, returns, standard deviations,
 * and variance
 */
@Service
public class PortfolioAnalyzeService {
    // the default initial fund to be used
    // when calculating holds and incomes
    final private static double INITIAL_FUND = 1_000_000;
    // the default component stock ticker
    // to be compared with the results of
    // portfolios
    final private static String COMPARE_TARGET = "SPY";

    final private PortfolioRiskService portfolioRiskService;
    final private PortfolioCalculatorService portfolioCalculatorService;
    final private ComponentStockHistoryService componentStockHistoryService;

    public PortfolioAnalyzeService(
            PortfolioRiskService portfolioRiskService,
            PortfolioCalculatorService portfolioCalculatorService,
            ComponentStockHistoryService componentStockHistoryService
    ) {
        this.portfolioRiskService = portfolioRiskService;
        this.portfolioCalculatorService = portfolioCalculatorService;
        this.componentStockHistoryService = componentStockHistoryService;
    }

    /**
     * Analyzes the provided portfolio and calculates all
     * required data
     * @param portfolio the portfolio to be analyzed
     * @return analyzed results
     */
    public PortfolioAnalyzeResult analyzePortfolio(Portfolio portfolio) {
        // stores component stock histories in map: {symbol: histories}
        Map<String, List<ComponentStockHistory>> componentStockHistories = new HashMap<>();
        // store component stock allocations in map: {symbol: allocation}
        Map<String, Double> componentStockAllocation = portfolio.getInvestment();
        // store component stock holds in map: {symbol: holds}
        Map<String, Integer> componentStockHolds = new HashMap<>();
        // set the comparing stock allocation to 1
        componentStockAllocation.put(COMPARE_TARGET, 1.0);
        // get the component stock histories of the comparing component stock
        var comparingHistories = this.componentStockHistoryService
                .queryComponentStockHistoryElapsedOneDay(
                        COMPARE_TARGET,
                        portfolio.getFrom(),
                        portfolio.getTo()
                );
        // put the comparing component stock histories into map
        componentStockHistories.put(COMPARE_TARGET, comparingHistories);
        // since all stocks have the same active days, component stock
        // histories should have the same size, use this as index range
        int historyCount = comparingHistories.size();
        // load all component stock histories based on portfolio
        for (var entry : componentStockAllocation.entrySet()) {
            var currComponentStockHistories = this.componentStockHistoryService
                    .queryComponentStockHistoryElapsedOneDay(
                            entry.getKey(),
                            portfolio.getFrom(),
                            portfolio.getTo()
                    );
            componentStockHistories.put(entry.getKey(), currComponentStockHistories);
        }
        // initialize income and return results
        List<AnalyzedResults<Double>> results = List.of(
                new AnalyzedResults<>(historyCount),        // portfolio income
                new AnalyzedResults<>(historyCount),        // portfolio return
                new AnalyzedResults<>(historyCount),        // comparing income
                new AnalyzedResults<>(historyCount)         // comparing return
        );
        // iterate through all component stock histories
        // and calculate the returns and incomes through iteration
        for (int index = 0; index < historyCount; ++ index) {
            // calculate allocated incomes and returns for every symbol
            for (var symbol : componentStockAllocation.keySet()) {
                // get current component stock histories
                var currHistory = componentStockHistories
                        .get(symbol)
                        .get(index);
                // the first iteration, for return and income, the first day
                // would always be 0, therefore no calculation is needed
                if (index == 0) {
                    // set 0s for all results
                    for (var result : results) {
                        // set allocated result and overall result
                        result.getAllocatedResults().get(symbol).add(
                                TimedResult.<Double>builder()
                                        .time(currHistory.getTime())
                                        .data(0.0)
                                        .build()
                        );
                        result.getResults().add(
                                TimedResult.<Double>builder()
                                        .time(currHistory.getTime())
                                        .data(0.0)
                                        .build()
                        );
                    }
                    // calculate holds for each stock based on allocation
                    componentStockHolds.put(symbol, (int) Math.floor(
                            INITIAL_FUND * componentStockAllocation.get(symbol) / currHistory.getClose()
                    ));
                } else {
                    // for the remaining iterations, calculate income and returns
                    // based on data
                    // get the history for the day before current
                    var prevHistory = componentStockHistories
                            .get(symbol)
                            .get(index - 1);
                    // calculate the income of current day
                    var currIncomeResult = this.portfolioCalculatorService
                            .calculateIncomeAllocated(
                                    currHistory,
                                    componentStockHolds.get(symbol),
                                    INITIAL_FUND,
                                    componentStockAllocation.get(symbol)
                            );
                    // calculate the return of current day
                    var currReturnResult = this.portfolioCalculatorService
                            .calculateReturnAllocated(
                                    currHistory,
                                    prevHistory,
                                    componentStockAllocation.get(symbol)
                            );
                    // save in a list for simplifying codes
                    var currCalculatedResults = List.of(
                            currIncomeResult,
                            currReturnResult
                    );
                    // this offset helps distinguish between comparing results and
                    // portfolio results, as 0 and 1 are portfolio results, and 2 and 3
                    // are comparing results
                    int indexOffset = symbol.equals(COMPARE_TARGET) ? 2 : 0;

                    // iterate through calculation results
                    for (
                            int calculatedResultIndex = 0;
                            calculatedResultIndex < currCalculatedResults.size();
                            ++ calculatedResultIndex
                    ) {

                        var currCalculatedResult = currCalculatedResults.get(calculatedResultIndex);
                        var currResultsObj = results.get(indexOffset + calculatedResultIndex);
                        // get current allocated results
                        var currAllocatedResults = currResultsObj.getAllocatedResults().get(symbol);
                        // add result directly, no need to consider conflicts
                        currAllocatedResults.add(currCalculatedResult);

                        // get current results
                        var currResults = currResultsObj.getResults();
                        var lastResult = currResults.get(currResults.size() - 1);
                        if (lastResult.getTime().equals(currHistory.getTime())) {
                            // if corresponding result already exists, add current allocated result to it
                            lastResult.setData(
                                    lastResult.getData() + currCalculatedResult.getData()
                            );
                        } else {
                            // corresponding result does not exist, set current allocated result as the result
                            currResults.add(currCalculatedResult);
                        }
                    }
                }
            }
        }

        // after iteration all results should be calculated

        Map<String, Double> allocatedOverallReturns = new HashMap<>();
        double overallReturns = 0;
        // calculate allocated overall returns and overall returns
        for (var symbol : componentStockAllocation.keySet()) {
            var currComponentStockHistories = componentStockHistories.get(symbol);
            // overall return = last day / first day - 1
            var currReturn = this.portfolioCalculatorService
                    .calculateReturnAllocated(
                            currComponentStockHistories.get(
                                    currComponentStockHistories.size() - 1
                            ),
                            currComponentStockHistories.get(0),
                            componentStockAllocation.get(symbol)
                    ).getData();

            allocatedOverallReturns.put(symbol, currReturn);
            overallReturns += currReturn;
        }

        // calculate standard deviation
        var standardDeviation = this.portfolioRiskService.calculateStandardDeviation(results.get(1));

        // calculate variances, we are interested in 90, 95, 99 % variances
        Map<Double, Double> variances = new HashMap<>();
        Stream.of(0.9, 0.95, 0.99).forEach(
                percent -> variances.put(
                        percent,
                        this.portfolioRiskService
                                .calculateVariance(
                                        results.get(1),
                                        percent
                                )
                                .getData()
                )
        );

        return PortfolioAnalyzeResult.builder()
                .portfolioIncomes(results.get(0))
                .portfolioReturns(results.get(1))
                .spyIncomes(results.get(2))
                .spyReturns(results.get(3))
                .allocatedOverallReturn(allocatedOverallReturns)
                .overallReturn(overallReturns)
                .standardDeviation(standardDeviation)
                .variances(variances)
                .build();
    }
}
