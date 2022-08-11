package com.sudo.portfolio.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import com.sudo.portfolio.datasource.RedisCache;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import com.sudo.portfolio.service.abstraction.IComponentStockHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;


/**
 * This class handles component stock histories from influxDB and to influxDB
 */
@Service
public class ComponentStockHistoryService implements IComponentStockHistoryService {

    private final Logger logger = LoggerFactory.getLogger(ComponentStockHistoryService.class);

    private final static int MAX_INSERTION_PER_EXECUTION = 65536;

    final private InfluxDBClient influxDBClient;
    final private ComponentStockHistoryRedisService componentStockHistoryRedisService;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Autowired
    public ComponentStockHistoryService(
            InfluxDBClient influxDBClient,
            ComponentStockHistoryRedisService componentStockHistoryRedisService
    ) {
        this.influxDBClient = influxDBClient;
        this.componentStockHistoryRedisService = componentStockHistoryRedisService;
    }

    /**
     * Add component stock history to influxDB
     * @param history component stock history
     */
    public void addComponentStockHistory(ComponentStockHistory history) {
        this.componentStockHistoryRedisService.addComponentStockHistory(history);
        this.influxDBClient
                .getWriteApiBlocking()
                .writeMeasurement(WritePrecision.NS, history);
    }

    /**
     * Add component stock histories to influxDB
     * @param histories component stock histories
     */
    public void addComponentStockHistories(List<ComponentStockHistory> histories) {
        logger.info("writing histories, total " + histories.size());
        this.componentStockHistoryRedisService.addComponentStockHistories(histories);

        if (histories.size() < MAX_INSERTION_PER_EXECUTION) {
            this.influxDBClient
                    .getWriteApiBlocking()
                    .writeMeasurements(WritePrecision.NS, histories);
        } else {
            int insertionId = 0;
            List<ComponentStockHistory> subHistories;

            while (insertionId * MAX_INSERTION_PER_EXECUTION < histories.size()) {
                logger.debug("writing insertion no. " + insertionId);

                subHistories = histories.subList(
                        insertionId * MAX_INSERTION_PER_EXECUTION,
                        Math.min((insertionId + 1) * MAX_INSERTION_PER_EXECUTION, histories.size())
                );

                this.influxDBClient
                        .getWriteApiBlocking()
                        .writeMeasurements(WritePrecision.NS, subHistories);

                ++insertionId;
            }
        }
    }

    /**
     * Query component stock histories from influxDB by time range
     * @param symbol symbol of component stock
     * @param start start timestamp
     * @param stop stop timestamp
     * @return list of queried component stocks
     */
    public List<ComponentStockHistory> queryComponentStockHistory(String symbol, Instant start, Instant stop) {
        var results = this.componentStockHistoryRedisService
                .queryComponentStockHistory(symbol, start, stop);

        if (results.size() != 0) return results;

        // cached missed
        Restrictions restrictions = Restrictions
                .tag("symbol")
                .equal(symbol);

        Restrictions nonnullRestriction = Restrictions
                .value().exists();

        Flux flux = Flux
                .from(this.bucket)
                .range(start, stop)
                .filter(restrictions)
                .filter(nonnullRestriction)
                .pivot(
                        new String[]{"_time"},
                        new String[]{"_field"},
                        "_value"
                );

        return this.influxDBClient
                    .getQueryApi()
                    .query(
                            flux.toString(),
                            ComponentStockHistory.class
                    );
    }

    public List<ComponentStockHistory> queryComponentStockHistoryElapsedOneDay(String symbol, Instant start, Instant stop) {
        var results = this.componentStockHistoryRedisService
                .queryComponentStockHistoryElapsedOneDay(symbol, start, stop);

        if (results.size() != 0) return results;

        Restrictions restrictions = Restrictions
                .tag("symbol")
                .equal(symbol);

        Restrictions nonnullRestriction = Restrictions
                .value().exists();

        Flux flux = Flux
                .from(this.bucket)
                .range(start, stop)
                .aggregateWindow(1L, ChronoUnit.DAYS, "last")
                .filter(restrictions)
                .filter(nonnullRestriction)
                .pivot(
                        new String[]{"_time"},
                        new String[]{"_field"},
                        "_value"
                );

        return this.influxDBClient
                .getQueryApi()
                .query(
                        flux.toString(),
                        ComponentStockHistory.class
                );
    }
}
