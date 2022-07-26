package com.sudo.portfolio.controller;

import com.sudo.portfolio.servicer.ComponentStockHistoryService;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ComponentStockHistoryController {

    final private ComponentStockHistoryService componentStockHistoryService;

    public ComponentStockHistoryController(
            ComponentStockHistoryService componentStockHistoryService
    ) {
        this.componentStockHistoryService = componentStockHistoryService;
    }
}
