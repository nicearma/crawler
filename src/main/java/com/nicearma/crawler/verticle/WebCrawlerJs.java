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

    /**
     * Because we want to continue to scan more url, so we will call the next url the next (timer) [s]
     * TODO: this is maybe better if we doit with rxJava
     */
    void askNextLink() {
        vertx.setTimer(timer, id -> {
            dbService.findLinkWithScannedStatus(CrawlerStatus.DISCOVERED).setHandler((result) -> {
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


    /**
     * Listen to SCAN_LINK, and i we have some link, open it at get information about it
     */
    void readLink() {

        vertx.eventBus().<JsonObject>consumer(StringConstants.SCAN_LINK).handler(m -> {

            try {
                //see if the driver is working
                verifyDriver();

                JsonObject jsonObject = m.body();
                //get the information from the body
                String href = jsonObject.getString(StringConstants.JSON_HREF);
                String url = jsonObject.getString(StringConstants.JSON_CRAWLER_URL);

                try {
                    //open the url
                    driver.get(href);
                } catch (Exception error) {
                    //something went wrong, try the next time
                    askNextLink();
                    m.fail(0, "");
                    return;
                }
                if (driver.getStatusCode() != 200) {
                    //something went wrong, try the next time
                    askNextLink();
                    m.fail(driver.getStatusCode(), "");
                    return;
                }
                //images pattern
                Pattern isImage = Regex.getPatternImage();
                //javascript or css pattern
                Pattern isJsOrCss = Regex.getPatternJsOrCss();
                //get all link
                List<WebElement> webElemens = driver.findElements(By.tagName("a"));

                List<String> links = new ArrayList<>();
                List<String> images = new ArrayList<>();
                //the list can be null or emptu
                if (webElemens != null && webElemens.size() > 0) {
                    //try to filter and only get useful link
                    webElemens.forEach(a -> {
                        //the the property href from <a>
                        String linkHref = a.getAttribute("href");
                        //the link is one image?
                        if (isImage.matcher(linkHref).matches()) {
                            //add it to the image list
                            images.add(linkHref);
                        } else if (!isJsOrCss.matcher(linkHref).matches()) { //not .css or .js
                            //is not the same url
                            if (!Regex.isSameUrl(linkHref)) {
                                //add the link
                                links.add(linkHref);
                            }
                        }
                    });
                }
                //get all images from the page
                webElemens = driver.findElements(By.tagName("img"));
                //we have images?
                if (webElemens != null && webElemens.size() > 0) {
                    //filter images
                    webElemens.forEach(img -> {
                        String imgSrc = img.getAttribute("src");
                        images.add(imgSrc);
                    });
                }
                //useful to see if we saved all information founded
                List<Future> futures = new ArrayList<>();
                //save link
                futures.add(dbService.insertLink(links, url));
                //save image
                futures.add(dbService.insertImage(images));
                //save relation between image and where was found
                CompositeFuture.all(futures).setHandler(r -> {
                    dbService.insertImageFoundAtLink(images, href);
                });
                //ask more url
                askNextLink();
                //replay every is ok
                m.reply("");
                return;

            } catch (Exception errorConsumer) {
                //ask for more url
                askNextLink();
                m.fail(0, errorConsumer.getMessage());
                return;
            }
        });

    }

    /**
     * Verify if the JBrowserDriver is working, if not close it and reopen another
     */
    void verifyDriver() {
        try {
            //if we created the objet and nothing is there, and if the driver is created and have some string value (verify if still working)
            if (driver == null || StringUtils.isEmpty(driver.toString())) {
                createDriver();
            }
        } catch (Exception error) {

            try {
                //the toString is not working, try to close the driver quit it
                driver.close();
                driver.quit();
            } catch (Exception errorQuit) {

            }
            //try to make a new one
            createDriver();

        }


    }

    /**
     * Create driver
     */
    private void createDriver() {
        driver = new JBrowserDriver(Settings.builder()
                //scan with the timezone of America
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
