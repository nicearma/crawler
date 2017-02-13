package com.nicearma.rest;


import com.nicearma.crawler.WebCrawler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.jboss.weld.context.activator.ActivateRequestContext;
import org.jboss.weld.vertx.web.WebRoute;

import javax.inject.Inject;

@WebRoute("/hello")
public class HelloHandlre implements Handler<RoutingContext> {

    @Inject
    WebCrawler webCrawler;

    @Inject
    Vertx vertx;





    @ActivateRequestContext // -> this interceptor binding may be used to activate the CDI request context within a handle() invocation
    @Override
    public void handle(RoutingContext ctx) {
        vertx.eventBus().send("scan.toScan", "https://www.nicearma.com");
        ctx.response().setStatusCode(200).end();


    }

}