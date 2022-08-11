package com.sudo.portfolio.integrity;


import com.sudo.portfolio.model.portfolio.TimedResult;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import com.sudo.portfolio.service.PortfolioCalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class PortfolioCalculatorIntegrityTests {

    @InjectMocks
    PortfolioCalculatorService portfolioCalculatorService;
    // 2022-08-08 12:00:00 UTC-7
    final Instant TIME = Instant.ofEpochMilli(1659985200000L);


    @Test
    public void testCalculateIncomeAllocated() {
        ComponentStockHistory currHistory = ComponentStockHistory.builder()
                .close(10.0)
                .open(5.0)
                .high(12.0)
                .low(5.0)
                .symbol("TEST")
                .volume(100L)
                .time(TIME)
                .build();

        int holds = 100;
        double initialFunds = 1_000_000;
        double allocation = 0.5;

        TimedResult<Double> result = TimedResult.<Double>builder()
                .time(TIME)
                .data(-499000.0)
                .build();

//        when(
//                portfolioCalculatorService.calculateIncomeAllocated(
//                        currHistory,
//                        holds,
//                        initialFunds,
//                        allocation
//                )
//        ).thenReturn(result);

        TimedResult<Double> actualResult = portfolioCalculatorService.calculateIncomeAllocated(
                currHistory,
                holds,
                initialFunds,
                allocation
        );

        assertEquals(result.getTime(), actualResult.getTime());
        assertEquals(result.getData(), actualResult.getData());
    }
}
