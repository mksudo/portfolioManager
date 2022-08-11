package com.sudo.portfolio.service;

import com.sudo.portfolio.model.portfolio.Portfolio;
import com.sudo.portfolio.model.portfolio.TimedResult;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * This class calculates income and return based on
 * input component stock histories
 */
@Service
public class PortfolioCalculatorService {

    /**
     * Determines whether the provided portfolio
     * performs better comparing to investing SPY.
     * @param portfolio the portfolio to be determined
     * @param from from time instant
     * @param to to time instant
     * @return whether the provided portfolio is better or not
     */
    public boolean isPortfolioBetterThanSPY(
            Portfolio portfolio,
            Instant from, Instant to
    ) {
        return true;
    }

    /**
     * Calculate the income based on the provided
     * component stock history close price and initial fund
     * @param currComponentStockHistory the component stock history to be used
     * @param holds how many holds do the portfolio
     *              buys on current component stock
     * @param initialFund how much fund the portfolio
     *                    provides at the beginning
     * @param allocation how many percent does current
     *                   component stock count in the final result
     * @return a timed result wrapping current component stock history time and income
     *         relative to the initial fund
     */
    public TimedResult<Double> calculateIncomeAllocated(
            ComponentStockHistory currComponentStockHistory,
            int holds,
            double initialFund,
            double allocation
    ) {
        double income = holds * currComponentStockHistory.getClose() - initialFund * allocation;

        return TimedResult.<Double>builder()
                .time(currComponentStockHistory.getTime())
                .data(income)
                .build();
    }

    /**
     * Calculate the return rate of current investment strategy
     * based on provided component stock histories of 2 days and
     * allocation
     * @param currComponentStockHistory more present day component stock history
     * @param prevComponentStockHistory earlier day component stock history
     * @param allocation how many percent does current
     *                   component stock count in the final result
     * @return a timed result wrapping current component stock history time and return
     */
    public TimedResult<Double> calculateReturnAllocated(
            ComponentStockHistory currComponentStockHistory,
            ComponentStockHistory prevComponentStockHistory,
            double allocation
    ) {
        // return = p2 / p1 - 1
        double returnValue = currComponentStockHistory.getClose() /
                prevComponentStockHistory.getClose() - 1;

        return TimedResult.<Double>builder()
                .time(currComponentStockHistory.getTime())
                .data(returnValue * allocation)
                .build();
    }

    public double calculateCorrelation(
            double[] portfolioResult,
            double[] comparedResult
    ) {
        return new PearsonsCorrelation()
                .correlation(portfolioResult, comparedResult);
    }
}
