package com.swapi.challenge.swapi.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwapiListResponse {
    private String message;
    private Integer total_records;
    private Integer total_pages;
    private String next;
    private String previous;
    @JsonAlias({ "results", "result" })
    private List<SwapiListItem> results;
}