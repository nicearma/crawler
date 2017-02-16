package com.nicearma;

import com.nicearma.crawler.UrlControl;
import com.nicearma.crawler.WebCrawlerJs;
import com.nicearma.db.DBService;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.jboss.weld.vertx.web.WeldWebVerticle;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {


    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        final WeldWebVerticle weldVerticle = new WeldWebVerticle();

        DeploymentOptions optionWorker = new DeploymentOptions();
        optionWorker.setWorker(true);

        vertx.deployVerticle(weldVerticle, result -> {

            //init database
            List<Future> results = new ArrayList<>();
            DBService dBService = weldVerticle.container().select(DBService.class).get();

            results.add(dBService.createTableImage());
            results.add(dBService.createTableUrl());

            CompositeFuture.all(results).setHandler((begin) -> {
                // Deploy Verticle instance produced by Weld
                vertx.deployVerticle(weldVerticle.container().select(UrlControl.class).get());

                vertx.deployVerticle(weldVerticle.container().select(WebCrawlerJs.class).get(), optionWorker);
                vertx.deployVerticle(weldVerticle.container().select(WebCrawlerJs.class).get(), optionWorker);
                vertx.deployVerticle(weldVerticle.container().select(WebCrawlerJs.class).get(), optionWorker);
                vertx.deployVerticle(weldVerticle.container().select(WebCrawlerJs.class).get(), optionWorker);

                Router router = Router.router(vertx);
                router.route().handler(BodyHandler.create());
                weldVerticle.registerRoutes(router);
                vertx.createHttpServer().requestHandler(router::accept).listen(8080);
            });


        });
    }


}
