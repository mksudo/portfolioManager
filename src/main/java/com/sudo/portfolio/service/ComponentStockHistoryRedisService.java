package com.sudo.portfolio.service;

import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import com.sudo.portfolio.service.abstraction.IComponentStockHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComponentStockHistoryRedisService implements IComponentStockHistoryService {
    final private RedisTemplate<Object, Object> redisTemplate;
    final private static String REDIS_KEY = "componentStockHistory";

    @Autowired
    public ComponentStockHistoryRedisService(
            RedisTemplate<Object, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    private String getCacheKey(String symbol) {
        return REDIS_KEY + "_" + symbol;
    }

    @SuppressWarnings("unchecked")
    private List<ComponentStockHistory> getCachedComponentStockHistories(
            String symbol
    ) {
        String cachedKey = this.getCacheKey(symbol);
        Boolean hasCachedKey = redisTemplate.hasKey(cachedKey);

        List<ComponentStockHistory> histories = new ArrayList<>();

        if (hasCachedKey != null && hasCachedKey) {

            Long historySize = this.redisTemplate
                    .boundListOps(cachedKey)
                    .size();
            if (historySize == null) return histories;

            var queryResult = this.redisTemplate
                    .boundListOps(cachedKey)
                    .range(0, historySize);

            if (queryResult == null) return histories;

            histories = (List<ComponentStockHistory>)(List<?>)queryResult;

        }

        return histories;
    }

    @Override
    public void addComponentStockHistory(ComponentStockHistory history) {
        this.redisTemplate
                .boundListOps(this.getCacheKey(history.getSymbol()))
                .rightPush(history);
    }

    @Override
    public void addComponentStockHistories(List<ComponentStockHistory> histories) {
        for (var history : histories) {
            this.redisTemplate
                    .boundListOps(this.getCacheKey(history.getSymbol()))
                    .rightPush(history);
        }
    }

    @Override
    public List<ComponentStockHistory> queryComponentStockHistory(String symbol, Instant start, Instant stop) {
        var histories = this.getCachedComponentStockHistories(symbol);

        return histories.stream().filter(
                history -> history.getTime().compareTo(start) > -1 &&
                        history.getTime().compareTo(stop) < 1
        ).collect(Collectors.toList());
    }

    @Override
    public List<ComponentStockHistory> queryComponentStockHistoryElapsedOneDay(String symbol, Instant start, Instant stop) {
        var histories = this.getCachedComponentStockHistories(symbol);
        var results = new ArrayList<ComponentStockHistory>();

        ComponentStockHistory prevHistory = null;

        for (var history : histories) {

            if (history.getTime().compareTo(start) < 0) continue;
            else if (history.getTime().compareTo(stop) > 0) break;

            if (
                    prevHistory == null ||
                            prevHistory
                                    .getTime()
                                    .plus(1L, ChronoUnit.DAYS)
                                    .equals(history.getTime())
            ) {
                results.add(history);
                prevHistory = history;
            }
        }

        return results;
    }
}
