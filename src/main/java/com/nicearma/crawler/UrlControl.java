package com.nicearma.crawler;

import com.nicearma.db.DBConnectorService;
import com.nicearma.db.DBService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


@Dependent
public class UrlControl extends AbstractVerticle {

    Logger logger = LoggerFactory.getLogger(UrlControl.class);

    List<String> urlScanneds;
    UrlControlConfiguration configuration;

    @Inject
    DBService dbService;

    public UrlControl() {
        this.urlScanneds = new ArrayList<>();
        configuration = new UrlControlConfiguration();
    }

    public UrlControl(UrlControlConfiguration configuration) {
        this.urlScanneds = new ArrayList<>();
        this.configuration = configuration;
    }

    @Override
    public void start() throws Exception {

        vertx.setPeriodic(5000, id -> {
            dbService.readUrlWithScannedStatus(false).setHandler((result) -> {
                result.result().getResults().forEach((a) -> {

                    logger.info(a.getString(0));
                });
            });

        });


        vertx.eventBus().consumer("scan.toScan").handler(m -> {


            String urlToScan = String.valueOf(m.body());

            if (StringUtils.isNotBlank(configuration.getUrlFilter()) && !configuration.equals(urlToScan)) {
                return;
            }
            if (urlToScan.contains("?share=")) {
                return;
            }

            boolean found = urlScanneds.stream().anyMatch(url -> url.equals(urlToScan));

            if (found) {
                return;
            } else {
                urlScanneds.add(urlToScan);
                vertx.eventBus().send("scan.url", urlToScan, (result) -> {
                    if (result.failed()) {
                        logger.info("fail:" + urlToScan, result.cause());
                    }
                });
            }


        });
    }

}
