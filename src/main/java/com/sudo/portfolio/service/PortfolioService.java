package com.sudo.portfolio.service;

import com.sudo.portfolio.datasource.PortfolioMapper;
import com.sudo.portfolio.model.portfolio.Portfolio;
import com.sudo.portfolio.service.abstraction.IPortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PortfolioService implements IPortfolioService {

    private final PortfolioMapper portfolioMapper;

    @Autowired
    public PortfolioService(
            PortfolioMapper portfolioMapper
    ) {
        this.portfolioMapper = portfolioMapper;
    }

    public int insertPortfolio(Portfolio portfolio) {
        if (portfolio.getId() == null) {
            portfolio.setId(UUID.randomUUID().toString());
        }

        int result = this.portfolioMapper.insertPortfolio(
                portfolio.getId(),
                portfolio.getFrom(),
                portfolio.getTo()
        );
        this.portfolioMapper.insertPortfolioInvestments(
                portfolio.getId(),
                portfolio.getInvestment()
        );

        return result;
    }

    public Portfolio selectPortfolio(String id) {
        var info = this.portfolioMapper.selectPortfolio(id);
        var investments = this.portfolioMapper.selectInvestments(id);

        if (info == null || investments == null) {
            return null;
        }

        Map<String, Double> mappedInvestments = new HashMap<>();

        for (var investment : investments) {
            mappedInvestments.put(
                    investment.getSymbol(),
                    investment.getAllocation()
            );
        }

        return Portfolio.builder()
                    .id(info.getId())
                    .from(info.getFrom())
                    .to(info.getTo())
                    .investment(mappedInvestments)
                    .build();
    }
}
