package com.nicearma;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration object, use this object to set defaults values
 */
@Data
public class Config {

    private int port = 8080;
    private int crawlerIntances;
    private String dataBaseUrl = "jdbc:h2:/tmp/db";
    private String dataBaseDriverClass = "org.h2.Driver";


    Config(String[] args) {
        crawlerIntances = Runtime.getRuntime().availableProcessors() * 2;
        Map<String, String> tmpConfig = makeMap(args);
        getPort(tmpConfig);
        getCrawlerIntances(tmpConfig);
        getDataBaseUrl(tmpConfig);
        getDataBaseDriverUrl(tmpConfig);
    }

    private Map<String, String> makeMap(String[] args) {
        Map<String, String> map = new HashMap();
        for (String arg : args) {
            if (arg.contains("=")) {
                map.put(arg.substring(0, arg.indexOf('=')),
                        arg.substring(arg.indexOf('=') + 1));
            }
        }

        return map;

    }

    private String getValue(Map<String, String> map, String key) {

        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    //TODO: try to reduce the same logic
    private void getPort(Map<String, String> tmpConfig) {
        String portString = getValue(tmpConfig, "PORT");
        if (StringUtils.isNoneBlank(portString)) {
            port = Integer.valueOf(portString);
        }
    }

    private void getCrawlerIntances(Map<String, String> tmpConfig) {
        String crawlerIntancesString = getValue(tmpConfig, "CRAWLER_INSTANCES");
        if (StringUtils.isNoneBlank(crawlerIntancesString)) {
            crawlerIntances = Integer.valueOf(crawlerIntancesString);
        }
    }

    private void getDataBaseUrl(Map<String, String> tmpConfig) {
        String dataBaseUrlString = getValue(tmpConfig, "DATA_BASE_URL");
        if (StringUtils.isNoneBlank(dataBaseUrlString)) {
            dataBaseUrl = dataBaseUrlString;
        }
    }

    private void getDataBaseDriverUrl(Map<String, String> tmpConfig) {
        String dataBaseDriverClassString = getValue(tmpConfig, "DATA_BASE_DRIVER_CLASS");
        if (StringUtils.isNoneBlank(dataBaseDriverClassString)) {
            dataBaseDriverClass = dataBaseDriverClassString;
        }
    }
}
