package com.sudo.portfolio.model.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;


/**
 * This class represents component stock data
 * in MySQL database
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentStock implements Serializable {
    /**
     * createTime the time when the component stock
     * data is first inserted into MySQL database
     */
    private Instant createTime;

    /**
     * lastUpdateTime the most recent time when the
     * component stock data is updated
     */
    private Instant lastUpdateTime;

    /**
     * state the state of the component stock data
     */
    private State state;

    /**
     * symbol the symbol of the component stock
     */
    private @NotNull String symbol;

    /**
     * security the security of the component stock
     */
    private @NotNull String security;

    /**
     * GICSSector the GICS Sector of the component stock
     */
    @JsonProperty("GICSSector")
    private @NotNull String GICSSector;

    /**
     * GICSSubIndustry the GICS Sub Industry of the component stock
     */
    @JsonProperty("GICSSubIndustry")
    private @NotNull String GICSSubIndustry;
}
