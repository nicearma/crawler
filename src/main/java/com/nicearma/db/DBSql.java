package com.nicearma.db;

public enum DBSql {

    /*--------------------CREATES-------------------*/

    /**
     *
     */
    CREATE_TABLE_IMAGE("CREATE TABLE IF NOT EXISTS image " +
            "( ID INTEGER auto_increment NOT NULL," +
            " src VARCHAR(1000)," +
            " name VARCHAR(1000)," +
            " CONSTRAINT image_src_unique PRIMARY KEY (src) )"),

    /**
     *
     */
    CREATE_TABLE_LINK("CREATE TABLE IF NOT EXISTS link" +
            " ( ID INTEGER auto_increment NOT NULL," +
            " href VARCHAR(1000)," +
            " crawler_url VARCHAR(1000)," +
            " status TINYINT DEFAULT 0," +
            " CONSTRAINT link_url_unique PRIMARY KEY (href)," +
            " FOREIGN KEY (crawler_url) REFERENCES crawler(url) )"),

    /**
     *
     */
    CREATE_TABLE_LINK_IMAGE("CREATE TABLE IF NOT EXISTS link_image" +
            " ( href VARCHAR(1000)," +
            " src VARCHAR(1000)," +
            " CONSTRAINT link_image_href_src_unique PRIMARY KEY (href,src)," +
            " FOREIGN KEY (src) REFERENCES image(src)," +
            " FOREIGN KEY (href) REFERENCES link(href) )"),

    /**
     *
     */
    CREATE_TABLE_CRAWLER("CREATE TABLE IF NOT EXISTS crawler" +
            " ( ID INTEGER auto_increment NOT NULL," +
            " url VARCHAR(1000)," +
            " filter_domain BOOLEAN DEFAULT true," +
            " domain_filter VARCHAR(1000)," +
            " filter_share_button BOOLEAN DEFAULT false," +
            " share_button_filter VARCHAR(1000)," +
            " CONSTRAINT crawler_url_unique PRIMARY KEY (url) )"),


    /*------------------SELECT---------------------*/
    /**
     *
     */
    SELECT_LINK_SCANNED_STATUS("SELECT href, crawler_url FROM link" +
            " WHERE status= ? " +
            " LIMIT ? OFFSET ?"),

    /**
     *
     */
    SELECT_CRAWLER_FROM_URL("SELECT * FROM crawler" +
            " WHERE url= ? "),


    /*-----------------INSERT----------------------*/

    /**
     *
     */
    INSERT_CRAWLER_WITH_ALL("MERGE INTO crawler (url,filter_domain, domain_filter, filter_share_button, share_button_filter)" +
            " VALUES (?,?,?,?,?)"),

    /**
     *
     */
    MERGE_FOUND_LINK("MERGE INTO link (href,crawler_url)" +
            " VALUES (?,?)"),

    /**
     *
     */
    MERGE_FOUND_IMAGE("MERGE INTO image (src)" +
            " VALUES (?)"),

    /**
     *
     */
    MERGE_FOUND_IMAGE_AT_LINK("MERGE INTO link_image (src,href)" +
            " VALUES (?,?)"),



    /*-----------------UPDATE----------------------*/

    /**
     *
     */
    UPDATE_LINK_SCANNED_URL("UPDATE link" +
            " SET status = ?" +
            " where href = ?");

    private String sql;

    DBSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

}
