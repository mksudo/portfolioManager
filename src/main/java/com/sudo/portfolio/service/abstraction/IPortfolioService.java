package com.sudo.portfolio.service.abstraction;

import com.sudo.portfolio.model.portfolio.Portfolio;

public interface IPortfolioService {

    int insertPortfolio(Portfolio portfolio);

    Portfolio selectPortfolio(String id);
}
