package com.sudo.portfolio.servicer;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


/**
 * This class handles component stock histories from influxDB and to influxDB
 */
@Service
public class ComponentStockHistoryService {

    final private InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Autowired
    public ComponentStockHistoryService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    /**
     * Add component stock history to influxDB
     * @param history component stock history
     */
    public void addComponentStockHistory(ComponentStockHistory history) {
        this.influxDBClient
                .getWriteApiBlocking()
                .writeMeasurement(WritePrecision.NS, history);
    }

    /**
     * Add component stock histories to influxDB
     * @param histories component stock histories
     */
    public void addComponentStockHistories(List<ComponentStockHistory> histories) {
        this.influxDBClient
                .getWriteApiBlocking()
                .writeMeasurements(WritePrecision.NS, histories);
    }

    /**
     * Query component stock histories from influxDB by time range
     * @param symbol symbol of component stock
     * @param start start timestamp
     * @param stop stop timestamp
     * @return list of queried component stocks
     */
    public List<ComponentStockHistory> queryComponentStockHistory(String symbol, Instant start, Instant stop) {
        Restrictions restrictions = Restrictions
                .tag("symbol")
                .equal(symbol);
        Flux flux = Flux
                .from(this.bucket)
                .range(start, stop)
                .filter(restrictions);
        return this.influxDBClient
                .getQueryApi()
                .query(
                        flux.toString(),
                        ComponentStockHistory.class
                );
    }
}
