package com.nicearma.db;

/**
 * Created by nicea on 23/10/2016.
 */
public enum DBSql {

    CREATE_TABLE_IMAGE( "CREATE TABLE IF NOT EXISTS image  ( ID INTEGER auto_increment NOT NULL, url varchar(255), name varchar(255) )"),
    CREATE_TABLE_URL( "CREATE TABLE IF NOT EXISTS crawl  ( ID INTEGER auto_increment NOT NULL, url varchar(255), scanned boolean )"),

    SELECT_UNSCANNED_URL("SELECT url FROM crawl WHERE scanned= ?  LIMIT ?,? ");

    private String sql;

    DBSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
