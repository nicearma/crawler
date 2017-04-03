package com.nicearma.rest;


import com.nicearma.crawler.CrawlerConfiguration;
import com.nicearma.crawler.verticle.WebCrawlerJs;
import com.nicearma.db.DBService;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.context.activator.ActivateRequestContext;
import org.jboss.weld.vertx.web.WebRoute;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@WebRoute(value = "/crawler", methods = HttpMethod.GET)
public class CrawlerHandler implements Handler<RoutingContext> {

    @Inject
    WebCrawlerJs webCrawler;

    @Inject
    DBService dbService;

    @Inject
    Vertx vertx;

    /**
     * TODO:
     *
     * @param ctx
     */
    @ActivateRequestContext
    @Override
    public void handle(RoutingContext ctx) {

        String url = ctx.request().getParam("url");
        String domain = ctx.request().getParam("domain");

        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(domain)) {
            ctx.response().setStatusCode(400).end();
            return;
        }

        CrawlerConfiguration crawlerConfiguration = new CrawlerConfiguration();

        crawlerConfiguration.setUrl(url);
        crawlerConfiguration.setDomainFilter(domain);
        crawlerConfiguration.setShareButtonFilter("share=");

        dbService.insertCrawler(crawlerConfiguration).setHandler(result -> {

            List<String> links = Arrays.asList(crawlerConfiguration.getUrl());
            dbService.insertLink(links, crawlerConfiguration.getUrl());
            ctx.response().setStatusCode(200).end();

        });


    }

}