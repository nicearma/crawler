package com.nicearma.crawler;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class UrlControlConfiguration {

    private String urlFilter="";
    private int deep=10;

}
