package com.sudo.portfolio.unit;


import com.sudo.portfolio.model.portfolio.AnalyzedResults;
import com.sudo.portfolio.model.portfolio.TimedResult;
import com.sudo.portfolio.service.PortfolioRiskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@ExtendWith(MockitoExtension.class)
public class PortfolioRiskServiceUnitTests {

    @Mock
    PortfolioRiskService portfolioRiskService;

    static final AnalyzedResults<Double> ANALYZED_RESULTS = new AnalyzedResults<>();

    @Test
    public void testCalculateStandardDeviation() {
        when(
                portfolioRiskService
                        .calculateStandardDeviation(ANALYZED_RESULTS)
        ).thenReturn(1.0);

        assertEquals(1.0, portfolioRiskService.calculateStandardDeviation(ANALYZED_RESULTS));
    }

    @Test
    public void testCalculateVariance() {
        Instant time = Instant.now();

        var result = TimedResult.<Double>builder()
                .time(time)
                .data(0.0)
                .build();

        when(
                portfolioRiskService
                        .calculateVariance(ANALYZED_RESULTS, 0.5)
        ).thenReturn(result);

        var actualResult = portfolioRiskService
                .calculateVariance(ANALYZED_RESULTS, 0.5);

        assertEquals(time, actualResult.getTime());
        assertEquals(0.0, actualResult.getData());
    }
}
