package com.sudo.portfolio.unit;

import com.sudo.portfolio.model.data.ComponentStock;
import com.sudo.portfolio.model.data.State;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import com.sudo.portfolio.service.ComponentStockHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class ComponentStockHistoryServiceUnitTests {

    @Mock
    ComponentStockHistoryService componentStockHistoryService;

    static final String SYMBOL_1 = "TEST_1";
    static final String SYMBOL_2 = "TEST_2";
    static final Instant TIME_1 = Instant.ofEpochMilli(1659985200000L);
    static final Instant TIME_2 = Instant.now();

    static final ComponentStockHistory HISTORY_1 = ComponentStockHistory.builder()
            .symbol(SYMBOL_1)
            .time(TIME_1)
            .open(0.0)
            .close(0.0)
            .high(0.0)
            .low(0.0)
            .volume(0L)
            .build();

    static final ComponentStockHistory HISTORY_2 = ComponentStockHistory.builder()
            .symbol(SYMBOL_2)
            .time(TIME_1)
            .open(0.0)
            .close(0.0)
            .high(0.0)
            .low(0.0)
            .volume(0L)
            .build();

    @Test
    public void testAddComponentStockHistory() {
        componentStockHistoryService.addComponentStockHistory(HISTORY_1);

        verify(
                componentStockHistoryService,
                times(1)
        ).addComponentStockHistory(HISTORY_1);
    }

    @Test
    public void testAddComponentStockHistories() {
        var componentStockHistories = List.of(
                HISTORY_1, HISTORY_2
        );
        componentStockHistoryService
                .addComponentStockHistories(componentStockHistories);

        verify(
                componentStockHistoryService,
                times(1)
        ).addComponentStockHistories(componentStockHistories);
    }

    @Test
    public void testQueryComponentStockHistory() {
        var result_1 = Collections.singletonList(HISTORY_1);
        var result_2 = Collections.singletonList(HISTORY_2);

        when(
                componentStockHistoryService
                        .queryComponentStockHistory(SYMBOL_1, TIME_1, TIME_2)
        ).thenReturn(result_1);
        when(
                componentStockHistoryService
                        .queryComponentStockHistory(SYMBOL_2, TIME_1, TIME_2)
        ).thenReturn(result_2);

        var actualResult_1 = componentStockHistoryService
                .queryComponentStockHistory(SYMBOL_1, TIME_1, TIME_2);

        assertEquals(1, actualResult_1.size());
        assertEquals(HISTORY_1, actualResult_1.get(0));

        var actualResult_2 = componentStockHistoryService
                .queryComponentStockHistory(SYMBOL_2, TIME_1, TIME_2);

        assertEquals(1, actualResult_2.size());
        assertEquals(HISTORY_2, actualResult_2.get(0));
    }

    @Test
    public void testQueryComponentStockHistoryElapsedOneDay() {
        var historyElapsedOneDay = ComponentStockHistory.builder()
                .symbol(SYMBOL_1)
                .time(TIME_1.plus(1, ChronoUnit.DAYS))
                .open(0.0)
                .close(0.0)
                .high(0.0)
                .low(0.0)
                .volume(0L)
                .build();

        var result = List.of(HISTORY_1, historyElapsedOneDay);

        when(
                componentStockHistoryService
                        .queryComponentStockHistoryElapsedOneDay(
                                SYMBOL_1,
                                TIME_1,
                                TIME_1.plus(1, ChronoUnit.DAYS)
                        )
        ).thenReturn(result);

        var actualResult = componentStockHistoryService
                .queryComponentStockHistoryElapsedOneDay(
                        SYMBOL_1,
                        TIME_1,
                        TIME_1.plus(1, ChronoUnit.DAYS)
                );

        assertEquals(2, actualResult.size());
        assertEquals(
                TIME_1,
                actualResult.get(0).getTime()
        );
        assertEquals(
                TIME_1.plus(1, ChronoUnit.DAYS),
                actualResult.get(1).getTime()
        );
    }
}
