package com.sudo.portfolio.model.yahoo;


/**
 * This enum represents the interval param
 * in yahoo finance api requests
 */
public enum Interval {
    /**
     * ONE_MINUTE interval of one minute
     */
    ONE_MINUTE("1m"),
    /**
     * TWO_MINUTE interval of two minutes
     */
    TWO_MINUTE("2m"),
    /**
     * FIVE_MINUTE interval of five minutes
     */
    FIVE_MINUTE("5m"),
    /**
     * FIFTEEN_MINUTE interval of fifteen minutes
     */
    FIFTEEN_MINUTE("15m"),
    /**
     * THIRTY_MINUTE interval of thirty minutes
     */
    THIRTY_MINUTE("30m"),
    /**
     * SIXTY_MINUTE interval of sixty minutes (1 hour)
     */
    SIXTY_MINUTE("60m"),
    /**
     * NINETY_MINUTE interval of ninety minutes (1.5 hours)
     */
    NINETY_MINUTE("90m"),
    /**
     * ONE_HOUR interval of one hour
     */
    ONE_HOUR("1h"),
    /**
     * ONE_DAY interval of one day
     */
    ONE_DAY("1d"),
    /**
     * FIVE_DAY interval of five days
     */
    FIVE_DAY("5d"),
    /**
     * ONE_WEEK interval of one week
     */
    ONE_WEEK("1wk"),
    /**
     * ONE_MONTH interval of one month
     */
    ONE_MONTH("1mo"),
    /**
     * THREE_MONTH interval of three months
     */
    THREE_MONTH("3mo");

    private final String interval;

    Interval(String interval) {
        this.interval = interval;
    }

    public String getInterval() {
        return this.interval;
    }
}
