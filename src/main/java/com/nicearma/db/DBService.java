package com.nicearma.db;

import com.nicearma.crawler.CrawlerConfiguration;
import com.nicearma.utils.CrawlerStatus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DBService {

    Logger logger = LoggerFactory.getLogger(DBService.class);

    private static JDBCClient jdbc;

    /**
     * TODO:
     *
     * @param crawlerConfiguration
     * @return
     */
    public Future insertCrawler(CrawlerConfiguration crawlerConfiguration) {
        Future future = Future.future();
        jdbc.getConnection(connection -> {
            if (connection.succeeded()) {
                connection.result().updateWithParams(DBSql.INSERT_CRAWLER_WITH_ALL.getSql(), crawlerConfiguration.toJsonArray(), (result) -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        logger.info(result.cause());
                    }
                }).close();
            }
        });
        return future;
    }

    /**
     * TODO:
     *
     * @param hrefs
     * @param url
     * @return
     */
    public Future insertLink(List<String> hrefs, String url) {
        Future future = Future.future();
        List<JsonArray> requestJson = hrefs.stream().map(href -> new JsonArray()
                .add(href)
                .add(url)
        ).collect(Collectors.toList());

        jdbc.getConnection(connection -> {
            if (connection.succeeded()) {
                connection.result().batchWithParams(DBSql.MERGE_FOUND_LINK.getSql(), requestJson, (result) -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        logger.info(result.cause());
                    }
                }).close();
            }
        });
        return future;
    }

    /**
     * TODO:
     *
     * @param srcs
     * @return
     */
    public Future insertImage(List<String> srcs) {
        Future future = Future.future();
        List<JsonArray> requestJson = srcs.stream().map(src -> new JsonArray()
                .add(src)
        ).collect(Collectors.toList());

        jdbc.getConnection(connection -> {
            if (connection.succeeded()) {
                connection.result().batchWithParams(DBSql.MERGE_FOUND_IMAGE.getSql(), requestJson, (result) -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        logger.info(result.cause());
                    }
                }).close();
            }
        });
        return future;
    }

    /**
     * TODO:
     *
     * @param srcs
     * @param href
     */
    public void insertImageFoundAtLink(List<String> srcs, String href) {

        List<JsonArray> requestJson = srcs.stream().map(src -> new JsonArray()
                .add(src)
                .add(href)
        ).collect(Collectors.toList());

        jdbc.getConnection(connection -> {
            if (connection.succeeded()) {
                connection.result().batchWithParams(DBSql.MERGE_FOUND_IMAGE_AT_LINK.getSql(), requestJson, (result) -> {
                    if (result.succeeded()) {

                    } else {
                        logger.info(result.cause());
                    }
                }).close();
            }
        });
    }

    /**
     * @param status
     * @return
     */
    public Future<ResultSet> readLinkWithScannedStatus(CrawlerStatus status) {

        Future<ResultSet> response = Future.future();

        jdbc.getConnection(connection -> {
            if (connection.succeeded()) {
                JsonArray requestJson = new JsonArray().add(status.getValue());
                addLimit(requestJson, 1, 0);
                connection.result().queryWithParams(DBSql.SELECT_LINK_SCANNED_STATUS.getSql(), requestJson, (result) -> {
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

    /**
     * @param url
     * @return
     */
    public Future<ResultSet> readCrawlerFromUrl(String url) {

        Future<ResultSet> response = Future.future();

        jdbc.getConnection(connection -> {
            if (connection.succeeded()) {
                JsonArray requestJson = new JsonArray().add(url);
                connection.result().queryWithParams(DBSql.SELECT_CRAWLER_FROM_URL.getSql(), requestJson, (result) -> {
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

    /**
     * @param status
     * @param url
     * @return
     */
    public Future<ResultSet> updateUrlScannedStatus(CrawlerStatus status, String url) {

        Future<ResultSet> response = Future.future();

        jdbc.getConnection(connection -> {
            if (connection.succeeded()) {
                JsonArray requestJson = new JsonArray()
                        .add(status.getValue())
                        .add(url);

                connection.result().updateWithParams(DBSql.UPDATE_LINK_SCANNED_URL.getSql(), requestJson, (result) -> {
                    if (result.succeeded()) {

                    } else {
                        logger.info(result.cause());
                    }

                }).close();
            }
        });
        return response;
    }

    /**
     * @param requestJson
     * @param limit
     * @param offset
     * @return
     */
    private JsonArray addLimit(JsonArray requestJson, int limit, int offset) {
        return requestJson.add(limit).add(offset);
    }


    /**
     * @return
     */
    public Future createDatabase() {
        //TODO:add chain
        Future response = Future.future();

        this.createTableCrawler().setHandler(resultCreateCrawler -> {

            List<Future> creates = new ArrayList<>();
            creates.add(this.createTableImage());
            creates.add(this.createTableLink());
            CompositeFuture.all(creates).setHandler(resultCreates -> {
                this.createTableLinkImage().setHandler(resultCreateLinkImage -> {
                    response.complete();
                });
            });
        });

        return response;
    }

    /**
     * TODO:
     * @return
     */
    private Future createTableImage() {
        return create(DBSql.CREATE_TABLE_IMAGE.getSql());
    }

    /**
     * TODO:
     * @return
     */
    private Future createTableCrawler() {
        return create(DBSql.CREATE_TABLE_CRAWLER.getSql());
    }

    /**
     * TODO
     * @return
     */
    private Future createTableLinkImage() {
        return create(DBSql.CREATE_TABLE_LINK_IMAGE.getSql());
    }

    /**
     * TODO
     * @return
     */
    private Future createTableLink() {
        return create(DBSql.CREATE_TABLE_LINK.getSql());
    }

    /**
     * TODO
     * @param sql
     * @return
     */
    private Future create(String sql) {
        Future response = Future.future();

        jdbc.getConnection(connection -> {
            if (connection.succeeded()) {
                connection.result().execute(sql, response.completer());
            } else {
                logger.error(connection.cause());
            }
        });
        return response;
    }

    /**
     *
     * @param url
     * @param driverClass
     */
    public static void createConnection(String url, String driverClass) {

        jdbc = JDBCClient.createShared(Vertx.vertx(), new JsonObject()
                .put("url", url)
                .put("driver_class", driverClass));
    }


}
