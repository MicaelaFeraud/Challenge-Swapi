package com.swapi.challenge.starship.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StarshipSummaryDto {
    private String id;
    private String name;
}
