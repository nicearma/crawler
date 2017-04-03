package com.nicearma.db;

public enum DBSql {

    /*--------------------CREATES-------------------*/

    /**
     * SQL to create the image table (used for save the src or href found from the crawler)
     */
    CREATE_TABLE_IMAGE("CREATE TABLE IF NOT EXISTS image " +
            "( ID INTEGER auto_increment NOT NULL," +
            " src VARCHAR(1000)," +
            " name VARCHAR(1000)," +
            " CONSTRAINT image_src_unique PRIMARY KEY (src) )"),

    /**
     * SQL to create the link table (used to save the src found from the crawler)
     */
    CREATE_TABLE_LINK("CREATE TABLE IF NOT EXISTS link" +
            " ( ID INTEGER auto_increment NOT NULL," +
            " href VARCHAR(1000)," +
            " crawler_url VARCHAR(1000)," +
            " status TINYINT DEFAULT 0," +
            " CONSTRAINT link_url_unique PRIMARY KEY (href)," +
            " FOREIGN KEY (crawler_url) REFERENCES crawler(url) )"),

    /**
     * SQL to create the link-image table (used to save the link-image relationship)
     */
    CREATE_TABLE_LINK_IMAGE("CREATE TABLE IF NOT EXISTS link_image" +
            " ( href VARCHAR(1000)," +
            " src VARCHAR(1000)," +
            " CONSTRAINT link_image_href_src_unique PRIMARY KEY (href,src)," +
            " FOREIGN KEY (src) REFERENCES image(src)," +
            " FOREIGN KEY (href) REFERENCES link(href) )"),

    /**
     * SQL to create the first crawler request (used to save useful information and configuration)
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
     * SQL to Get all link with given status
     */
    SELECT_LINK_SCANNED_STATUS("SELECT href, crawler_url FROM link" +
            " WHERE status= ? " +
            " LIMIT ? OFFSET ?"),

    /**
     * SQL to get the crawler information/configuration
     */
    SELECT_CRAWLER_FROM_URL("SELECT * FROM crawler" +
            " WHERE url= ? "),


    /*-----------------INSERT----------------------*/

    /**
     * SQL to save the first crawler information/configuration
     */
    INSERT_CRAWLER_WITH_ALL("MERGE INTO crawler (url,filter_domain, domain_filter, filter_share_button, share_button_filter)" +
            " VALUES (?,?,?,?,?)"),

    /**
     * SQL to save the href found by the crawler
     */
    MERGE_FOUND_LINK("MERGE INTO link (href,crawler_url)" +
            " VALUES (?,?)"),

    /**
     * SQL to save the src/href found by the crawler
     */
    MERGE_FOUND_IMAGE("MERGE INTO image (src)" +
            " VALUES (?)"),

    /**
     * SQL to save the relationship between the link-image
     */
    MERGE_FOUND_IMAGE_AT_LINK("MERGE INTO link_image (src,href)" +
            " VALUES (?,?)"),



    /*-----------------UPDATE----------------------*/

    /**
     * SQL to update the link status
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
