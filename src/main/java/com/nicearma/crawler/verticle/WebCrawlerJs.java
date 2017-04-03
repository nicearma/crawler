package com.nicearma.crawler.verticle;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.nicearma.db.DBService;
import com.nicearma.utils.CrawlerStatus;
import com.nicearma.utils.Regex;
import com.nicearma.utils.StringConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Dependent
public class WebCrawlerJs extends AbstractVerticle {

    private static int count = 0;
    private long timer;

    Logger logger = LoggerFactory.getLogger(WebCrawlerJs.class);

    @Inject
    DBService dbService;

    JBrowserDriver driver;

    public WebCrawlerJs() {
        count++;
        timer = count * 100;
    }

    @Override
    public void start() throws Exception {
        createDriver();
        readLink();
        askNextLink();
    }


    void askNextLink() {
        vertx.setTimer(timer, id -> {
            dbService.readLinkWithScannedStatus(CrawlerStatus.DISCOVERED).setHandler((result) -> {
                if (result.result().getRows().size() > 0) {

                    result.result().getRows().forEach((a) -> {
                        dbService.updateUrlScannedStatus(CrawlerStatus.SCANNING, a.getString(StringConstants.JSON_HREF));
                        vertx.eventBus().send(StringConstants.SCAN_TO_SCAN, a, (resultToScan) -> {

                            if (resultToScan.failed()) {
                                askNextLink();
                            }
                        });
                    });
                } else {
                    askNextLink();
                }

            });

        });
    }


    void readLink() {


        vertx.eventBus().<JsonObject>consumer(StringConstants.SCAN_LINK).handler(m -> {

            try {

                verifyDriver();

                logger.info("thread id: " + Thread.currentThread().getName());

                JsonObject jsonObject = m.body();

                String href = jsonObject.getString(StringConstants.JSON_HREF);
                String url = jsonObject.getString(StringConstants.JSON_CRAWLER_URL);

                try {
                    driver.get(href);
                } catch (Exception error) {
                    askNextLink();
                    m.fail(0, "");
                    return;
                }
                if (driver.getStatusCode() != 200) {
                    askNextLink();
                    m.fail(driver.getStatusCode(), "");
                    return;
                }

                Pattern isImage = Regex.getPatternImage();

                Pattern isJsOrCss = Regex.getPatternJsOrCss();

                List<WebElement> webElemens = driver.findElements(By.tagName("a"));
                List<String> links = new ArrayList<>();
                List<String> images = new ArrayList<>();

                if (webElemens != null && webElemens.size() > 0) {
                    webElemens.forEach(a -> {
                        String linkHref = a.getAttribute("href");
                        if (isImage.matcher(linkHref).matches()) {
                            images.add(linkHref);
                        } else if (!isJsOrCss.matcher(linkHref).matches()) {
                            if (!Regex.isSameUrl(linkHref)) {
                                links.add(linkHref);
                            }
                        }
                    });
                }

                webElemens = driver.findElements(By.tagName("img"));

                if (webElemens != null && webElemens.size() > 0) {
                    webElemens.forEach(img -> {
                        String imgSrc = img.getAttribute("src");
                        images.add(imgSrc);
                    });
                }
                //driver.close();
                //logger.info(driver.mediaDir().getAbsolutePath());

                List<Future> futures = new ArrayList<>();

                futures.add(dbService.insertLink(links, url));
                futures.add(dbService.insertImage(images));
                CompositeFuture.all(futures).setHandler(r -> {
                    dbService.insertImageFoundAtLink(images, href);
                });
                askNextLink();
                m.reply("");
                return;

            } catch (Exception errorConsumer) {
                askNextLink();
                m.fail(0, errorConsumer.getMessage());
                return;
            }
        });

    }


    void verifyDriver() {
        try {

            if (driver == null || StringUtils.isEmpty(driver.toString())) {
                createDriver();
            }
        } catch (Exception error) {

            try {
                driver.close();
                driver.quit();
            } catch (Exception errorQuit) {

            }
            createDriver();

        }


    }

    private void createDriver() {
        driver = new JBrowserDriver(Settings.builder()
                .timezone(Timezone.AMERICA_NEWYORK)
                .hostnameVerification(false)
                .logWarnings(false)
                // .connectTimeout(2000)
                //.connectionReqTimeout(2000)
                //.ajaxResourceTimeout(1000)
                //.socketTimeout(1000)
                .saveMedia(true)
                .blockAds(true)
                .cache(true)
                .build());
    }


}
