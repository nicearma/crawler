package com.nicearma.crawler;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import lombok.extern.java.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.enterprise.context.Dependent;
import java.util.List;

@Dependent
public class WebCrawler extends AbstractVerticle {

    JBrowserDriver driver;
    Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    @Override
    public void start() throws Exception {

        vertx.eventBus().consumer("scan.url").handler(m -> {

            driver = new JBrowserDriver(Settings.builder().
                    timezone(Timezone.AMERICA_NEWYORK).build());


            String url = String.valueOf(m.body());

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
