package com.nicearma.utils;

/**
 * Useful to fallow every step of link
 */
public enum CrawlerStatus {

    /**
     * The first status
     */
    DISCOVERED(0),

    /**
     * We are scanning
     */
    SCANNING(1),

    /**
     * Already scanned/finish scanning
     */
    CRAWLED(2),

    /**
     * Is ignored (normally by the filter)
     */
    INGORED(3),

    /**
     * Something is weird
     */
    UNKNONW(-1),

    /**
     * Something went wrong
     */
    ERROR(-2);

    /**
     * The value of the status (used at the SQL)
     */
    int status;

    CrawlerStatus(int status) {
        this.status = status;
    }

    public int getValue() {
        return status;
    }

}
