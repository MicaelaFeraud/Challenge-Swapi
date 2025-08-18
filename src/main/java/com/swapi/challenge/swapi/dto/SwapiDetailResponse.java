package com.swapi.challenge.swapi.dto;

import lombok.Data;

@Data
public class SwapiDetailResponse {
    private String message;
    private SwapiResult result;
}