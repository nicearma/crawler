package com.nicearma.crawler.verticle;

import com.nicearma.crawler.CrawlerConfiguration;
import com.nicearma.db.DBService;
import com.nicearma.utils.CrawlerStatus;
import com.nicearma.utils.Regex;
import com.nicearma.utils.StringConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.regex.Pattern;


@Dependent
public class UrlControl extends AbstractVerticle {

    Logger logger = LoggerFactory.getLogger(UrlControl.class);


    @Inject
    DBService dbService;


    @Override
    public void start() throws Exception {

        vertx.eventBus().<JsonObject>consumer(StringConstants.SCAN_TO_SCAN).handler(m -> {
            JsonObject jsonObject = m.body();

            //TODO:add some cache
            dbService.readCrawlerFromUrl(jsonObject.getString(StringConstants.JSON_CRAWLER_URL)).setHandler(resultReadCrawlerFromUrl -> {
                if (resultReadCrawlerFromUrl.succeeded()) {
                    CrawlerConfiguration crawlerConfiguration = new CrawlerConfiguration(resultReadCrawlerFromUrl.result().getRows().get(0));

                    if (crawlerConfiguration.isFilterDomain() && StringUtils.isNotBlank(crawlerConfiguration.getDomainFilter())) {

                        Pattern inDomain = Regex.getPatternDomain(crawlerConfiguration.getDomainFilter());


                        if (!inDomain.matcher(jsonObject.getString(StringConstants.JSON_HREF)).matches()) {

                            logger.info("Ignored another domain:" + jsonObject.getString(StringConstants.JSON_HREF));
                            dbService.updateUrlScannedStatus(CrawlerStatus.INGORED, jsonObject.getString(StringConstants.JSON_HREF));
                            m.fail(0, "");
                            return;
                        }
                    }

                    if (crawlerConfiguration.isFilterShareButton() && StringUtils.isNotBlank(crawlerConfiguration.getShareButtonFilter()) && jsonObject.getString(StringConstants.JSON_HREF).contains(crawlerConfiguration.getShareButtonFilter())) {
                        logger.info("Ignored share button :" + jsonObject.getString(StringConstants.JSON_HREF));
                        dbService.updateUrlScannedStatus(CrawlerStatus.INGORED, crawlerConfiguration.getUrl());
                        m.fail(0, "");
                        return;
                    }

                    logger.info("scanning:" + jsonObject.getString(StringConstants.JSON_HREF));

                    vertx.eventBus().send(StringConstants.SCAN_LINK, jsonObject, (result) -> {
                        if (result.succeeded()) {
                            dbService.updateUrlScannedStatus(CrawlerStatus.CRAWLED, jsonObject.getString(StringConstants.JSON_HREF));
                        } else {
                            if (result.failed()) {
                                dbService.updateUrlScannedStatus(CrawlerStatus.ERROR, jsonObject.getString(StringConstants.JSON_HREF));
                                logger.info("fail:" + jsonObject.getString(StringConstants.JSON_HREF), result.cause());
                            }
                        }

                    });
                    m.reply("");
                }
            });


        });
    }

}
