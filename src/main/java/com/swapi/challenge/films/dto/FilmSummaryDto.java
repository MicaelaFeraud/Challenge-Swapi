package com.swapi.challenge.films.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmSummaryDto {
    private String id;
    private String title;
}
