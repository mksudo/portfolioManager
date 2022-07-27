package com.sudo.portfolio.controller;

import com.sudo.portfolio.model.Portfolio;
import com.sudo.portfolio.servicer.PortfolioCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@CrossOrigin
@RestController
public class PortfolioController {

    private final PortfolioCalculatorService portfolioCalculatorService;

    @Autowired
    public PortfolioController(
            PortfolioCalculatorService portfolioCalculatorService
    ) {
        this.portfolioCalculatorService = portfolioCalculatorService;
    }

    @RequestMapping("/portfolio")
    public boolean checkPortfolio(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to,
            @RequestBody Portfolio portfolio
    ) {
        return this.portfolioCalculatorService.isPortfolioBetterThanSPY(
                portfolio,
                from,
                to
        );
    }
}
