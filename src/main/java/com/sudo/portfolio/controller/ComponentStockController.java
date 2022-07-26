package com.sudo.portfolio.controller;

import com.sudo.portfolio.model.data.ComponentStock;
import com.sudo.portfolio.servicer.ComponentStockCrawlerService;
import com.sudo.portfolio.servicer.ComponentStockHistoryService;
import com.sudo.portfolio.servicer.ComponentStockService;
import com.sudo.portfolio.servicer.YahooCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class ComponentStockController {

    final private YahooCrawlerService yahooFinanceCrawlerService;
    final private ComponentStockService componentStockService;
    final private ComponentStockCrawlerService componentStockCrawlerService;
    final private ComponentStockHistoryService componentStockHistoryService;


    @Autowired
    public ComponentStockController(
            YahooCrawlerService yahooFinanceCrawlerService,
            ComponentStockService componentStockService,
            ComponentStockCrawlerService componentStockCrawlerService,
            ComponentStockHistoryService componentStockHistoryService
    ) {
        this.yahooFinanceCrawlerService = yahooFinanceCrawlerService;
        this.componentStockService = componentStockService;
        this.componentStockCrawlerService = componentStockCrawlerService;
        this.componentStockHistoryService = componentStockHistoryService;
    }

    @RequestMapping("/find")
    public ComponentStock findComponentStock(
            @RequestParam(value = "symbol") String symbol
    ) {
        return this.componentStockService.selectComponentStock(symbol);
    }

    @RequestMapping("/find/all")
    public List<ComponentStock> findAllComponentStocks() {
        return this.componentStockService.selectComponentStocks();
    }

    @RequestMapping("/add")
    public void addComponentStock(
            @RequestBody @Validated ComponentStock componentStock
    ) {
        this.componentStockService.insertComponentStock(componentStock);
    }

    @RequestMapping("/delete")
    public void deleteComponentStock(
            @RequestParam(value = "symbol") String symbol
    ) {
        this.componentStockService.deleteComponentStock(symbol);
    }

    @RequestMapping("/update")
    public void updateComponentStock(
            @RequestBody @Validated ComponentStock componentStock
    ) {
        this.componentStockService.updateComponentStock(componentStock);
    }
}
