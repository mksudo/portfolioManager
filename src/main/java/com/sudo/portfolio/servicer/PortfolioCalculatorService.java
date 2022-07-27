package com.sudo.portfolio.servicer;

import com.sudo.portfolio.model.Portfolio;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class PortfolioCalculatorService {

    private static final double INITIAL_FUND = 1_000_000;
    private final ComponentStockHistoryService componentStockHistoryService;

    @Autowired
    public PortfolioCalculatorService(
            ComponentStockHistoryService componentStockHistoryService
    ) {
        this.componentStockHistoryService = componentStockHistoryService;
    }

    private List<ComponentStockHistory> getSPYHistoryTimePeriod(Instant from, Instant to) {
        return this.componentStockHistoryService.queryComponentStockHistory("SPY", from, to);
    }

    private double calculateComponentStockIncome(
            double initialFund,
            List<ComponentStockHistory> componentStockHistories
    ) {
        componentStockHistories.sort(Comparator.comparing(ComponentStockHistory::getTime));

        int holds;
        double currFund = initialFund;

        for (var history : componentStockHistories) {
            // buy in when stock market opens
            holds = (int) Math.floor(currFund / history.getOpen());
            // sell out when stock market closes
            currFund = holds * history.getClose();
        }

        return currFund;
    }

    private double calculateIncomeFromPortfolio(
            Portfolio portfolio,
            Instant from, Instant to
    ) {
        double finalFund = 0;
        for (var entry : portfolio.getInvestment().entrySet()) {
            var componentStockHistories =
                    this.componentStockHistoryService.queryComponentStockHistory(
                            entry.getKey(),
                            from,
                            to
                    );

            finalFund += this.calculateComponentStockIncome(
                    INITIAL_FUND * entry.getValue(),
                    componentStockHistories
            );
        }

        return finalFund;
    }

    public boolean isPortfolioBetterThanSPY(
            Portfolio portfolio,
            Instant from, Instant to
    ) {

        double spyIncome = this.calculateComponentStockIncome(
                INITIAL_FUND,
                this.getSPYHistoryTimePeriod(from, to)
        );

        double portfolioIncome = this.calculateIncomeFromPortfolio(
                portfolio,
                from,
                to
        );

        return portfolioIncome > spyIncome;
    }
}
