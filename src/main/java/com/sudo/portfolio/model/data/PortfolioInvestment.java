package com.sudo.portfolio.model.data;

import lombok.Data;


@Data
public class PortfolioInvestment {
    /**
     * id id of portfolio
     */
    private String id;

    private String symbol;

    private Double allocation;
}
