package com.nicearma.crawler;

import com.nicearma.utils.StringConstants;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CrawlerConfiguration {
    /**
     * The root url to be scanned
     */
    private String url;
    /**
     * Use filter domain, only the same domain will be scanned
     */
    private boolean filterDomain = true;
    /**
     *
     */
    private String domainFilter;
    /**
     * User filter shared button, if the url have share(d)=value, will not be included
     */
    private boolean filterShareButton = true;
    /**
     * Normally the shared button will have share(d)=value
     */
    private String shareButtonFilter;
    //TODO: not used at this moment
    /**
     * The deep scan logic (not used at this moment)
     */
    private int deep = 10;

    public CrawlerConfiguration() {

    }

    /**
     * Convert from JsonObject to CrawlerConfiguration
     *
     * @param jsonObject
     */
    public CrawlerConfiguration(JsonObject jsonObject) {
        this.url = jsonObject.getString(StringConstants.JSON_URL);
        this.filterDomain = jsonObject.getBoolean(StringConstants.JSON_FILTER_DOMAIN);
        this.domainFilter = jsonObject.getString(StringConstants.JSON_DOMAIN_FILTER);
        this.filterShareButton = jsonObject.getBoolean(StringConstants.JSON_FILTER_SHARE_BUTTON);
        this.shareButtonFilter = jsonObject.getString(StringConstants.JSON_SHARE_BUTTON_FILTER);
        //this.deep = jsonObject.getInteger(StringConstants.JSON_DEEP);
    }

    /**
     * Convert from CrawlerConfiguration to JsonObject
     *
     * @return
     */
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

    /**
     * Convert CrawlerConfiguration to JsonArray, at this moment we have
     * [0] url
     * [1] filterDomain
     * [2] domainFilter
     * [3] filterShareButton
     * [4] shareButtonFilter
     *
     * @return
     */
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
