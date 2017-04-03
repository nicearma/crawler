package com.nicearma.crawler;

import com.nicearma.utils.StringConstants;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CrawlerConfiguration {

    private String url;
    private boolean filterDomain = true;
    private String domainFilter;
    private boolean filterShareButton = true;
    private String shareButtonFilter;
    //TODO: not used at this moment
    private int deep = 10;

    public CrawlerConfiguration() {

    }

    public CrawlerConfiguration(JsonObject jsonObject) {
        this.url = jsonObject.getString(StringConstants.JSON_URL);
        this.filterDomain = jsonObject.getBoolean(StringConstants.JSON_FILTER_DOMAIN);
        this.domainFilter = jsonObject.getString(StringConstants.JSON_DOMAIN_FILTER);
        this.filterShareButton = jsonObject.getBoolean(StringConstants.JSON_FILTER_SHARE_BUTTON);
        this.shareButtonFilter = jsonObject.getString(StringConstants.JSON_SHARE_BUTTON_FILTER);
        //this.deep = jsonObject.getInteger(StringConstants.JSON_DEEP);
    }


    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(StringConstants.JSON_URL, url);
        jsonObject.put(StringConstants.JSON_FILTER_DOMAIN, filterDomain);
        jsonObject.put(StringConstants.JSON_DOMAIN_FILTER, domainFilter);
        jsonObject.put(StringConstants.JSON_FILTER_SHARE_BUTTON, filterShareButton);
        jsonObject.put(StringConstants.JSON_SHARE_BUTTON_FILTER, shareButtonFilter);
        //jsonObject.put(StringConstants.JSON_DEEP, deep);
        return toJsonObject();
    }


    public JsonArray toJsonArray() {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(url);
        jsonArray.add(filterDomain);
        jsonArray.add(domainFilter);
        jsonArray.add(filterShareButton);
        jsonArray.add(shareButtonFilter);
        return jsonArray;
    }


}
