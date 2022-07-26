package com.sudo.portfolio.model.data;


/**
 * This enum represents the state of a data in the database
 */
public enum State {
    /**
     * OK represents a valid data in the database
     */
    OK("OK"),
    /**
     * OUTDATED represents an invalid data
     * that is overwritten by a newer value
     */
    OUTDATED("OUTDATED"),
    /**
     * DELETED represents an invalid data
     * that is explicitly deleted
     */
    DELETED("DELETED");

    private final String state;

    State(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
