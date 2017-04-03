package com.nicearma.rest;


import com.nicearma.crawler.CrawlerConfiguration;
import com.nicearma.crawler.verticle.WebCrawlerJs;
import com.nicearma.db.DBService;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.jboss.weld.context.activator.ActivateRequestContext;
import org.jboss.weld.vertx.web.WebRoute;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@WebRoute("/hello")
public class CrawlerHandler implements Handler<RoutingContext> {

    @Inject
    WebCrawlerJs webCrawler;

    @Inject
    DBService dbService;

    @Inject
    Vertx vertx;

    /**
     *
     * @param ctx
     */
    @ActivateRequestContext
    @Override
    public void handle(RoutingContext ctx) {

        CrawlerConfiguration crawlerConfiguration = new CrawlerConfiguration();
        crawlerConfiguration.setUrl("http://localhost:8080/");
        crawlerConfiguration.setDomainFilter("localhost");
        crawlerConfiguration.setShareButtonFilter("share=");

        dbService.insertCrawler(crawlerConfiguration).setHandler(result -> {

            List<String> links = Arrays.asList(crawlerConfiguration.getUrl());
            dbService.insertLink(links, crawlerConfiguration.getUrl());
            ctx.response().setStatusCode(200).end();

        });


    }

}