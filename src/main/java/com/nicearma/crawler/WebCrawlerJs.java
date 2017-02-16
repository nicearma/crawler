package com.nicearma.crawler;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.nicearma.db.DBService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.openqa.selenium.By;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Dependent
public class WebCrawlerJs extends AbstractVerticle {


    Logger logger = LoggerFactory.getLogger(WebCrawlerJs.class);

    @Inject
    DBService dBService;


    @Override
    public void start() throws Exception {

        vertx.eventBus().consumer("scan.url").handler(m -> {

            JBrowserDriver driver = new JBrowserDriver(Settings.builder().
                    timezone(Timezone.AMERICA_NEWYORK).build());

            String url = String.valueOf(m.body());

            driver.get(url);

            List<String> links = driver.findElements(By.tagName("a")).stream().map(a -> a.getAttribute("href")).collect(Collectors.toList());

            driver.quit();

            links.forEach((l)->{
                logger.info(l);
            });

            dBService.insertUrl(links);

        });
    }


}
