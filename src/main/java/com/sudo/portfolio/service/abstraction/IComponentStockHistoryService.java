package com.sudo.portfolio.service.abstraction;

import com.sudo.portfolio.model.yahoo.ComponentStockHistory;

import java.time.Instant;
import java.util.List;

public interface IComponentStockHistoryService {
    void addComponentStockHistory(ComponentStockHistory history);

    void addComponentStockHistories(List<ComponentStockHistory> histories);

    List<ComponentStockHistory> queryComponentStockHistory(String symbol, Instant start, Instant stop);

    List<ComponentStockHistory> queryComponentStockHistoryElapsedOneDay(String symbol, Instant start, Instant stop);
}
