package com.sudo.portfolio.model;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;


/**
 * This class represents a portfolio to be calculated
 */
@Data
@Builder
public class Portfolio {
    /**
     * id id of portfolio
     */
    private @NotNull String id;

    /**
     * from the time to start simulating investment
     */
    private @NotNull Instant from;

    /**
     * to the time to stop simulating investment
     */
    private @NotNull Instant to;

    /**
     * investment stores investment percentages by (symbol: percentage)
     */
    private @NotNull Map<String, Double> investment;

    /**
     * Validate current investment, check whether the percentages sum up to
     * 100% or not
     * @return whether the percentages sum up to 100% or not
     */
    private boolean validateInvestment() {
        return this.investment
                .values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum() == 1d;
    }

    /**
     * Validate current time period, check whether from is smaller than to or not
     * @return whether from is smaller than to or not
     */
    private boolean validateTimePeriod() {
        return this.from.compareTo(to) < 0;
    }

    /**
     * Validate current portfolio, check whether all data is valid or not
     * @return whether current portfolio is valid or not
     */
    public boolean validate() {
        return this.validateTimePeriod() &&
                this.validateInvestment();
    }
}
