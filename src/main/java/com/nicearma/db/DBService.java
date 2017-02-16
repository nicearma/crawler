package com.nicearma.db;

import com.nicearma.crawler.WebCrawlerJs;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class DBService {

    Logger logger = LoggerFactory.getLogger(DBService.class);

    @Inject
    DBConnectorService dBConnectorService;

    public void insertUrl(List<String> urls) {
        List<JsonArray> requestJson = urls.stream().map(url -> new JsonArray().add(url)).collect(Collectors.toList());
        dBConnectorService.getJdbc().getConnection(connection -> {
            if (connection.succeeded()) {
                connection.result().batchWithParams(DBSql.INSERT_FOUND_URL.getSql(), requestJson, (result) -> {
                    if (result.succeeded()) {

                    } else {
                        logger.info(result.cause());
                    }
                }).close();
            }
        });
    }

    public Future<ResultSet> readUrlWithScannedStatus(boolean scanned) {
        Future<ResultSet> response = Future.future();
        dBConnectorService.getJdbc().getConnection(connection -> {
            if (connection.succeeded()) {
                JsonArray requestJson = new JsonArray().add(scanned);
                //addLimit(requestJson, 5, 0);
                connection.result().queryWithParams(DBSql.SELECT_URL_SCANNED_STATUS.getSql(), requestJson, (result) -> {
                    if (result.succeeded()) {
                        response.complete(result.result());
                    } else {
                        logger.info(result.cause());
                    }

                }).close();
            }
        });
        return response;
    }

    private JsonArray addLimit(JsonArray requestJson, int size, int page) {
        return requestJson.add(size).add(page);
    }

    public Future createTableImage() {
        return create(DBSql.CREATE_TABLE_IMAGE.getSql());
    }

    public Future createTableUrl() {
        return create(DBSql.CREATE_TABLE_URL.getSql());

    }

    private Future create(String sql) {
        Future response = Future.future();

        response.setHandler((result) -> {

        });
        dBConnectorService.getJdbc().getConnection(connection -> {
            if (connection.succeeded()) {
                connection.result().execute(sql, response.completer());
            }
        });
        return response;
    }


}
