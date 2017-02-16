package com.nicearma.db;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;


public class DBConnectorService {


    JDBCClient jdbc;

    public DBConnectorService(){

        jdbc= JDBCClient.createShared(Vertx.vertx(), new JsonObject()
                .put("url", "jdbc:h2:crawler-db")
                .put("driver_class", "org.h2.Driver"));
    }

    public JDBCClient getJdbc() {
        return jdbc;
    }
}
