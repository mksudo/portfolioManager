package com.sudo.portfolio.model.data;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Instant;


@Data
public class PortfolioInfo {
    /**
     * id id of portfolio
     */
    private String id;

    /**
     * from the time to start simulating investment
     */
    private @NotNull Instant from;

    /**
     * to the time to stop simulating investment
     */
    private @NotNull Instant to;
}
