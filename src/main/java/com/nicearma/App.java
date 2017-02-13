package com.nicearma;

import com.nicearma.crawler.UrlControl;
import com.nicearma.crawler.WebCrawler;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.jboss.weld.vertx.web.WeldWebVerticle;

/**
 * Hello world!
 */
public class App {


    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        final WeldWebVerticle weldVerticle = new WeldWebVerticle();

        DeploymentOptions optionWorker = new DeploymentOptions();
        optionWorker.setWorker(true);
        optionWorker.setWorkerPoolSize(8);

        vertx.deployVerticle(weldVerticle, result -> {
            if (result.succeeded()) {
                // Deploy Verticle instance produced by Weld
                vertx.deployVerticle(weldVerticle.container().select(UrlControl.class).get());
                vertx.deployVerticle(weldVerticle.container().select(WebCrawler.class).get(), optionWorker);

                Router router = Router.router(vertx);
                router.route().handler(BodyHandler.create());
                weldVerticle.registerRoutes(router);
                vertx.createHttpServer().requestHandler(router::accept).listen(8080);

            }
        });
    }


}
