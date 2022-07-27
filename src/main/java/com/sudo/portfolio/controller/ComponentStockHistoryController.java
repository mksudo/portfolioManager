package com.sudo.portfolio.controller;

import com.sudo.portfolio.model.data.ComponentStock;
import com.sudo.portfolio.model.postBody.ComponentStockHistoryRequest;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import com.sudo.portfolio.model.yahoo.Interval;
import com.sudo.portfolio.model.yahoo.Range;
import com.sudo.portfolio.servicer.ComponentStockCrawlerService;
import com.sudo.portfolio.servicer.ComponentStockHistoryService;
import com.sudo.portfolio.servicer.YahooCrawlerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Instant;
import java.util.List;


@CrossOrigin
@RestController
public class ComponentStockHistoryController {

    final private YahooCrawlerService yahooCrawlerService;
    final private ComponentStockCrawlerService componentStockCrawlerService;
    final private ComponentStockHistoryService componentStockHistoryService;

    public ComponentStockHistoryController(
            ComponentStockCrawlerService componentStockCrawlerService,
            ComponentStockHistoryService componentStockHistoryService,
            YahooCrawlerService yahooCrawlerService
    ) {
        this.componentStockCrawlerService = componentStockCrawlerService;
        this.componentStockHistoryService = componentStockHistoryService;
        this.yahooCrawlerService = yahooCrawlerService;
    }

    @RequestMapping(value = "/history/crawl", params = {"range"})
    public void crawlComponentStockHistoriesByRange(
            @RequestParam("range") Range range,
            @RequestBody
            @Validated
            @NotNull
            ComponentStockHistoryRequest componentStockHistoryRequest
    ) {
        this.componentStockHistoryService.addComponentStockHistories(
                this.yahooCrawlerService.getStockHistories(
                        componentStockHistoryRequest.getSymbols(),
                        range,
                        componentStockHistoryRequest.getInterval()
                )
        );
    }

    @RequestMapping(value = "/history/crawl", params = {"from", "to"})
    public void crawlComponentStockHistoriesByTimePeriod(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to,
            @RequestBody
            ComponentStockHistoryRequest componentStockHistoryRequest
    ) {
        this.componentStockHistoryService.addComponentStockHistories(
                this.yahooCrawlerService.getStockHistories(
                        componentStockHistoryRequest.getSymbols(),
                        from,
                        to,
                        componentStockHistoryRequest.getInterval()
                )
        );
    }

    @RequestMapping("/history/crawl/all")
    public void crawlAllComponentStockHistories(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to,
            @RequestParam("interval")
            String interval
    ) throws IOException {
        this.componentStockHistoryService.addComponentStockHistories(
                this.yahooCrawlerService.getStockHistories(
                        this.componentStockCrawlerService
                                .crawlComponentStocks()
                                .stream()
                                .map(ComponentStock::getSymbol)
                                .toList(),
                        from,
                        to,
                        Interval.fromText(interval)
                )
        );
    }

    @RequestMapping("/history/find")
    public List<ComponentStockHistory> findBySymbolAndTimePeriod(
            @RequestParam("symbol") String symbol,
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to
    ) {
        return this.componentStockHistoryService.queryComponentStockHistory(symbol, from, to);
    }
}
