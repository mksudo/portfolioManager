package com.sudo.portfolio.unit;

import com.sudo.portfolio.model.data.ComponentStock;
import com.sudo.portfolio.model.data.State;
import com.sudo.portfolio.service.ComponentStockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class ComponentStockServiceUnitTests {

    @Mock
    ComponentStockService componentStockService;

    static final String SYMBOL = "TEST";
    static final String SECURITY = "SECURITY";
    static final String GICS_SECTOR = "GICS_SECTOR";
    static final String GICS_SUB_INDUSTRY = "GICS_SUB_INDUSTRY";
    static final Instant TIME = Instant.ofEpochMilli(1659985200000L);

    static final ComponentStock RESULT = ComponentStock.builder()
            .symbol(SYMBOL)
            .security(SECURITY)
            .GICSSector(GICS_SECTOR)
            .GICSSubIndustry(GICS_SUB_INDUSTRY)
            .createTime(TIME)
            .lastUpdateTime(TIME)
            .state(State.OK)
            .build();

    static final ComponentStock ANOTHER_RESULT = ComponentStock.builder()
            .symbol(SYMBOL)
            .security(SECURITY)
            .GICSSector(GICS_SECTOR)
            .GICSSubIndustry(GICS_SUB_INDUSTRY)
            .createTime(TIME)
            .lastUpdateTime(TIME)
            .state(State.OK)
            .build();

    @Test
    public void testSelectComponentStock() {
        when(
                componentStockService
                        .selectComponentStock(SYMBOL)
        ).thenReturn(RESULT);

        var actualResult = componentStockService.selectComponentStock(SYMBOL);

        assertEquals(SYMBOL, actualResult.getSymbol());
        assertEquals(SECURITY, actualResult.getSecurity());
        assertEquals(GICS_SECTOR, actualResult.getGICSSector());
        assertEquals(GICS_SUB_INDUSTRY, actualResult.getGICSSubIndustry());
        assertEquals(TIME, actualResult.getCreateTime());
        assertEquals(TIME, actualResult.getLastUpdateTime());
        assertEquals(State.OK, actualResult.getState());
    }

    @Test
    public void testSelectComponentStocks() {
        List<ComponentStock> result = Collections.singletonList(RESULT);

        when(
                componentStockService
                        .selectComponentStocks()
        ).thenReturn(result);

        var actualResult = componentStockService.selectComponentStocks();

        assertEquals(1, actualResult.size());
        verify(componentStockService, times(1)).selectComponentStocks();
    }

    @Test
    public void testUpdateComponentStock() {
        Instant currTime = Instant.now();

        var updatedResult = ComponentStock.builder()
                .symbol(SYMBOL)
                .security(SECURITY)
                .GICSSector(GICS_SECTOR)
                .GICSSubIndustry(GICS_SUB_INDUSTRY)
                .createTime(TIME)
                .lastUpdateTime(currTime)
                .state(State.OK)
                .build();

        when(
                componentStockService
                        .updateComponentStock(updatedResult)
        ).thenReturn(1);

        assertEquals(
                1,
                componentStockService
                        .updateComponentStock(updatedResult)
        );
    }

    @Test
    public void testInsertComponentStock() {
        when(
                componentStockService
                        .insertComponentStock(RESULT)
        ).thenReturn(1);

        var actualResult = componentStockService
                .insertComponentStock(RESULT);

        assertEquals(1, actualResult);
    }

    @Test
    public void testInsertComponentStocks() {
        List<ComponentStock> componentStocks = List.of(RESULT, ANOTHER_RESULT);

        when(
                componentStockService
                        .insertComponentStocks(componentStocks)
        ).thenReturn(2);

        var actualResult = componentStockService
                .insertComponentStocks(componentStocks);

        assertEquals(2, actualResult);
    }

    @Test
    public void testDeleteComponentStock() {
        when(
                componentStockService
                        .deleteComponentStock(SYMBOL)
        ).thenReturn(1);

        var actualResult = componentStockService
                .deleteComponentStock(SYMBOL);

        assertEquals(
                1,
                actualResult
        );
    }
}
