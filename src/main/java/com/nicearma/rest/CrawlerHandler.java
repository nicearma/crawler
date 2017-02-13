package com.nicearma.rest;


import com.nicearma.crawler.WebCrawlerJs;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.jboss.weld.context.activator.ActivateRequestContext;
import org.jboss.weld.vertx.web.WebRoute;

import javax.inject.Inject;

@WebRoute("/hello")
public class CrawlerHandler implements Handler<RoutingContext> {

    @Inject
    WebCrawlerJs webCrawler;

    @Inject
    Vertx vertx;

    @ActivateRequestContext
    @Override
    public void handle(RoutingContext ctx) {
        vertx.eventBus().send("scan.toScan", "https://www.nicearma.com");
        ctx.response().setStatusCode(200).end();


    }

}