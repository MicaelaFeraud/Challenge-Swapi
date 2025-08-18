package com.swapi.challenge.swapi;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "swapi")
public class SwapiClientProperties {
    private String baseUrl;
    private int defaultPageSize = 10;
    private int bulkLimit = 100;
}
