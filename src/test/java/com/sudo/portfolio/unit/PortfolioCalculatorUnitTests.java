package com.sudo.portfolio.unit;

import com.sudo.portfolio.model.portfolio.TimedResult;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import com.sudo.portfolio.service.PortfolioCalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@ExtendWith(MockitoExtension.class)
public class PortfolioCalculatorUnitTests {

    @Mock
    PortfolioCalculatorService portfolioCalculatorService;
    final static Instant TIME = Instant.ofEpochMilli(1659985200000L);

    final static ComponentStockHistory DATA = ComponentStockHistory.builder()
            .symbol("TEST")
            .time(TIME)
            .open(0.0)
            .close(0.0)
            .high(0.0)
            .low(0.0)
            .volume(0L)
            .build();

    @Test
    public void testCalculateIncomeAllocated() {
        var result = TimedResult.<Double>builder()
                .time(TIME)
                .data(0.0)
                .build();

        when(
                portfolioCalculatorService.calculateIncomeAllocated(
                        DATA,
                        100,
                        1_000_000,
                        0.5
                )
        ).thenReturn(result);

        var actualResult = portfolioCalculatorService.calculateIncomeAllocated(
                DATA,
                100,
                1_000_000,
                0.5
        );
        assertEquals(TIME, actualResult.getTime());
        assertEquals(0.0, actualResult.getData());
    }

    @Test
    public void testCalculateReturnAllocated() {
        Instant currTime = Instant.now();

        var nextData = ComponentStockHistory.builder()
                .symbol("TEST")
                .time(currTime)
                .open(0.0)
                .close(0.0)
                .high(0.0)
                .low(0.0)
                .volume(0L)
                .build();

        var result = TimedResult.<Double>builder()
                .time(currTime)
                .data(0.0)
                .build();

        when(
                portfolioCalculatorService.calculateReturnAllocated(
                        nextData,
                        DATA,
                        0.5
                )
        ).thenReturn(result);

        var actualResult = portfolioCalculatorService.calculateReturnAllocated(
                nextData,
                DATA,
                0.5
        );

        assertNotEquals(TIME, actualResult.getTime());
        assertEquals(currTime, actualResult.getTime());
        assertEquals(0.0, actualResult.getData());
    }
}
