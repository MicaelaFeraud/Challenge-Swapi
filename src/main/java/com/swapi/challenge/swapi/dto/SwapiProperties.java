package com.swapi.challenge.swapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiProperties {
    // People
    private String name;
    private String gender;
    private String height;
    private String mass;
    private String birth_year;

    // Films
    private String title;
    private String director;
    private String producer;
    private String release_date;

    // Starships/Vehicles
    private String model;
    private String manufacturer;
    private String starship_class;
    private String vehicle_class;

}
