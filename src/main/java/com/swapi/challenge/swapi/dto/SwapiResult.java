package com.swapi.challenge.swapi.dto;

import lombok.Data;

@Data
public class SwapiResult {
    private String uid;
    private SwapiProperties properties;
    private String description;
}
