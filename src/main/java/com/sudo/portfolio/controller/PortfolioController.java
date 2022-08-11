package com.sudo.portfolio.controller;

import com.sudo.portfolio.model.portfolio.Portfolio;
import com.sudo.portfolio.model.portfolio.PortfolioAnalyzeResult;
import com.sudo.portfolio.service.PortfolioAnalyzeService;
import com.sudo.portfolio.service.PortfolioCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class PortfolioController {

    private final PortfolioCalculatorService portfolioCalculatorService;
    private final PortfolioAnalyzeService portfolioAnalyzeService;

    @Autowired
    public PortfolioController(
            PortfolioCalculatorService portfolioCalculatorService,
            PortfolioAnalyzeService portfolioAnalyzeService
    ) {
        this.portfolioCalculatorService = portfolioCalculatorService;
        this.portfolioAnalyzeService = portfolioAnalyzeService;
    }

    @RequestMapping("/portfolio")
    public boolean checkPortfolio(
            @RequestBody Portfolio portfolio
    ) {
        return this.portfolioCalculatorService.isPortfolioBetterThanSPY(
                portfolio,
                portfolio.getFrom(),
                portfolio.getTo()
        );
    }

    @RequestMapping(value = "/portfolio/analyze", method = RequestMethod.POST)
    public PortfolioAnalyzeResult analyzePortfolio(
            @RequestBody Portfolio portfolio
    ) {
        return this.portfolioAnalyzeService.analyzePortfolio(portfolio);
    }
}
