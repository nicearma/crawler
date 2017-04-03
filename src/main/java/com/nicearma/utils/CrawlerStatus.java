package com.nicearma.utils;

/**
 *
 */
public enum CrawlerStatus {
    /**
     *
     */
    DISCOVERED(0),
    /**
     *
     */
    SCANNING(1),
    /**
     *
     */
    CRAWLED(2),
    /**
     *
     */
    INGORED(3),
    /**
     *
     */
    UNKNONW(-1),
    /**
     *
     */
    ERROR(-2);
    /**
     *
     */
    int status;

    CrawlerStatus(int status) {
        this.status = status;
    }


    public int getValue() {
        return status;
    }

}
