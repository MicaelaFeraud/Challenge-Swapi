package com.swapi.challenge.api;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto<T> {
    private List<T> items;
    private Integer page;
    private Integer limit;
    private Integer totalPages;
    private Integer totalRecords;
    private String next;
    private String previous;
}