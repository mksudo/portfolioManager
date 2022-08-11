package com.sudo.portfolio.datasource;

import com.sudo.portfolio.model.data.PortfolioInfo;
import com.sudo.portfolio.model.data.PortfolioInvestment;
import com.sudo.portfolio.model.portfolio.Portfolio;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@Mapper
public interface PortfolioMapper {

    int insertPortfolio(String id, Instant from, Instant to);

    int insertPortfolioInvestment(String id, String symbol, double allocation);

    int insertPortfolioInvestments(String id, Map<String, Double> investments);

    @MapKey("symbol")
    List<PortfolioInvestment> selectInvestments(String id);

    PortfolioInfo selectPortfolio(String id);
}
