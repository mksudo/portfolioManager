package com.sudo.portfolio.model.yahoo;

/**
 * This enum represents the range param
 * in yahoo finance api requests
 */
public enum Range {
    /**
     * ONE_DAY range of one day
     */
    ONE_DAY("1d"),
    /**
     * FIVE_DAY range of five days
     */
    FIVE_DAY("5d"),
    /**
     * ONE_MONTH range of one month
     */
    ONE_MONTH("1mo"),
    /**
     * THREE_MONTH range of three months
     */
    THREE_MONTH("3mo"),
    /**
     * SIX_MONTH range of six months
     */
    SIX_MONTH("6mo"),
    /**
     * ONE_YEAR range of one year
     */
    ONE_YEAR("1y"),
    /**
     * TWO_YEAR range of two years
     */
    TWO_YEAR("2y"),
    /**
     * FIVE_YEAR range of five years
     */
    FIVE_YEAR("5y"),
    /**
     * TEN_YEAR range of ten years
     */
    TEN_YEAR("10y"),
    /**
     * YTD range ytd
     */
    YTD("ytd"),
    /**
     * MAX range max
     */
    MAX("max");

    private final String range;

    Range(String range) {
        this.range = range;
    }

    public String getRange() {
        return this.range;
    }
}
