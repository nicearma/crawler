package com.nicearma.crawler;

import com.nicearma.db.DBConnectorService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


@Dependent
public class CrawlerJob extends AbstractVerticle {

    Logger logger = LoggerFactory.getLogger(CrawlerJob.class);
    @Inject
    DBConnectorService dBConnectorService;

    @Override
    public void start() throws Exception {

        vertx.setPeriodic(5000, id -> {

            dBConnectorService.getJdbc().getConnection(connection -> {
                if (connection.succeeded()) {

                } else {
                    logger.error(connection.cause());
                }
            });


        });

    }

}
