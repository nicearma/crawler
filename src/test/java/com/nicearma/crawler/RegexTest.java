package com.nicearma.crawler;


import com.nicearma.utils.Regex;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Pattern;

public class RegexTest {

    private static final String domain = "nicearma.com";

    private static final String[] goodUrls = {
            "http://www.nicearma.com",
            "https://www.nicearma.com",
            "https://www.nicearma.com/",
            "https://www.nicearma.com/category/"
    };

    private static final String[] badUrls = {
            "http://anotherdomain.com",
            "http://www.anotherdomain.com",
            "http://www.google.com",
            "https://loadus.exelator.com/load/?p=531&g=001&j=w&puid=c8lmgu7j394jqm2",
            "https://bcp.crwdcntrl.net/5/c=574/pv=y/tp=DISQ/tpid=c8lmgu7j394jqm2",
            "https://tags.bluekai.com/site/23096?limit=1&phint=__bk_pr%3Dhttps%3A%2F%2Fwww.nicearma.com%2F2011%2F10%2F14%2Fnueva-version-de-ubuntu-11-10%2F&phint=category%3D10"
    };

    private static final String[] jsUrls = {
            "http://www.nicearma.com/javascript.js",
            "http://www.nicearma.com/javascript.js?ver=2.3",
            "https://www.nicearma.com/wp-includes/js/jquery/jquery-migrate.min.js?ver=1.4.1"
    };

    private static final String[] cssUrls = {
            "http://www.nicearma.com/javascript.css",
            "http://www.nicearma.com/javascript.css?ver=2.3"
    };

    private static final String[] imageUrls = {
            "http://www.nicearma.com/image.png",
            "https://www.nicearma.com/image.png",
            "https://nicearma.com/image.png",
            "http://nicearma.com/image.png",
            "http://nicearma.com/image.jpg",
            "http://nicearma.com/image.gif",
            "http://nicearma.com/image.jpeg",
            "http://nicearma.com/image.svg"
    };

    private static final String[] sameUrls = {
            "http://www.nicearma.com/",
            "http://www.nicearma.com/#nicearma",
            "http://www.nicearma.com/?more=nicearma"
    };


    @Test
    public void ignoreUrl() {


    }

    @Test
    public void passUrl() {
        Pattern isGoodDomain = Regex.getPatternDomain(domain);
        for (String url : goodUrls) {
            Assert.assertTrue(isGoodDomain.matcher(url).matches());
        }

        for (String url : badUrls) {
            Assert.assertFalse(isGoodDomain.matcher(url).matches());
        }
    }

    @Test
    public void filterUrlImage() {
        Pattern isImage = Regex.getPatternImage();
        for (String url : imageUrls) {
            Assert.assertTrue(isImage.matcher(url).matches());
        }
    }

    @Test
    public void filterUrlCss() {
        Pattern isImage = Regex.getPatternJsOrCss();
        for (String url : cssUrls) {
            Assert.assertTrue(isImage.matcher(url).matches());
        }
    }

    @Test
    public void filterUrlJs() {
        Pattern isImage = Regex.getPatternJsOrCss();
        for (String url : jsUrls) {
            Assert.assertTrue(isImage.matcher(url).matches());
        }
    }


    @Test
    public void filterUrlSame() {
        String baseUrl = sameUrls[0];
        for (String url : sameUrls) {
            if (!baseUrl.equals(url)) {
                Assert.assertTrue(Regex.isSameUrl(baseUrl, url));
            }
        }

        for (String url : sameUrls) {
            if (!baseUrl.equals(url)) {
                Assert.assertTrue(Regex.isSameUrl(url));
            }
        }

        for (String url : goodUrls) {
            if (!baseUrl.equals(url)) {
                Assert.assertTrue(!Regex.isSameUrl(url));
            }
        }
    }


}
