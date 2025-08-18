package com.swapi.challenge.films.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmDetailDto {
    private String id;
    private String title;
    private String director;
    private String producer;
    private String releaseDate;
}
