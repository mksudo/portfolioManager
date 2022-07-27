package com.sudo.portfolio.model.yahoo;


import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


/**
 * This class represents a single history of component stock
 * open close high low volume data from yahoo finance api,
 * will be stored in snp500 measurement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Measurement(name = "snp500")
public class ComponentStockHistory {
    /**
     * time timestamp of component stock history,
     * will also be the timestamp of the corresponding point
     * in influxdb
     */
    @Column(timestamp = true)
    private Instant time;

    /**
     * symbol the symbol of component stock
     */
    @Column(tag = true)
    private String symbol;

    /**
     * low low price of component stock at that time
     */
    @Column
    private Double low;

    /**
     * high high price of component stock at that time
     */
    @Column
    private Double high;

    /**
     * open open price of component stock at that time
     */
    @Column
    private Double open;

    /**
     * close close price of component stock at that time
     */
    @Column
    private Double close;

    /**
     * volume volume of component stock that day
     */
    @Column
    private Long volume;
}
