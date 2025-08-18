package com.swapi.challenge.starship.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StarshipDetailDto {
    private String id;
    private String name;
    private String model;
    private String manufacturer;
    private String starshipClass;
}
