package com.nicearma.crawler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.List;


@Dependent
public class UrlControl extends AbstractVerticle {

    Logger logger = LoggerFactory.getLogger(UrlControl.class);

    List<String> urlScanneds;
    UrlControlConfiguration configuration;

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

            // This handler will get called every second
            //urlScanneds.forEach(logger::info);

        });



        vertx.eventBus().consumer("scan.toScan").handler(m -> {


            String urlToScan = String.valueOf(m.body());

            if (StringUtils.isNotBlank(configuration.getUrlFilter())&& !configuration.equals(urlToScan)) {
                return;
            }
            if(urlToScan.contains("?share=")){
                return;
            }

            boolean found=urlScanneds.stream().anyMatch(url -> url.equals(urlToScan));

            if(found){
                return;
            }else{
                urlScanneds.add(urlToScan);
                vertx.eventBus().send("scan.url", urlToScan,(result)->{
                   if(result.failed()){
                       logger.info("fail:"+urlToScan, result.cause());
                   }
                });
            }


        });
    }

}
