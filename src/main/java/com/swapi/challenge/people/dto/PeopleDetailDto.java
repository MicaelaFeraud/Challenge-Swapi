package com.swapi.challenge.people.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PeopleDetailDto {
    private String id;
    private String name;
    private String gender;
    private String height;
    private String mass;
    private String birthYear;
}
