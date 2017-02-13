package com.nicearma.crawler;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.nicearma.db.DBConnectorService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class WebCrawlerJs extends AbstractVerticle {


    Logger logger = LoggerFactory.getLogger(WebCrawlerJs.class);

    @Inject
    DBConnectorService dBConnectorService;


    @Override
    public void start() throws Exception {

        vertx.eventBus().consumer("scan.url").handler(m -> {

            JBrowserDriver driver = new JBrowserDriver(Settings.builder().
                    timezone(Timezone.AMERICA_NEWYORK).build());


            String url = String.valueOf(m.body());
            //logger.info("scanned:"+url);

            // This will block for the page load and any
            // associated AJAX requests
            driver.get(url);

            // You can get status code unlike other Selenium drivers.
            // It blocks for AJAX requests and page loads after clicks
            // and keyboard events.
            System.out.println(driver.getStatusCode());

            List<WebElement> links = driver.findElements(By.tagName("a"));

            links.forEach(link -> scan(link.getAttribute("href")));



            driver.quit();

        });
    }

    public void scan(String url){
        vertx.eventBus().send("scan.toScan", url);
    }


}
