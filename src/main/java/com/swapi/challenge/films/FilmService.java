package com.swapi.challenge.films;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.films.dto.FilmDetailDto;
import com.swapi.challenge.films.dto.FilmSummaryDto;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final SwapiClient swapi;

    public FilmService(SwapiClient swapi) {
        this.swapi = swapi;
    }

    public PageDto<FilmSummaryDto> list(Integer page, Integer limit, String name, String id) {
        if (id != null && !id.trim().isEmpty()) {
            FilmDetailDto d = getById(id.trim());
            List<FilmSummaryDto> items = (d == null)
                    ? new ArrayList<>()
                    : new ArrayList<>(Collections.singletonList(new FilmSummaryDto(d.getId(), d.getTitle())));

            int total = items.size();
            int l = (limit == null || limit < 1) ? 10 : limit;
            Integer totalPages = (total == 0) ? 0 : 1;

            return new PageDto<>(
                    items,
                    1,
                    l,
                    totalPages,
                    total,
                    null,
                    null
            );
        }

        SwapiListResponse sw = swapi.listFilms(page, limit, name);
        List<SwapiListItem> src = sw != null && sw.getResults() != null ? sw.getResults() : Collections.emptyList();

        List<FilmSummaryDto> items = src.stream()
                .map(it -> new FilmSummaryDto(it.getUid(), it.getName()))
                .collect(Collectors.toList());

        Integer totalPages   = sw != null ? sw.getTotal_pages()   : null;
        Integer totalRecords = sw != null ? sw.getTotal_records() : null;

        return new PageDto<>(items, page, limit, totalPages, totalRecords, sw != null ? sw.getNext() : null, sw != null ? sw.getPrevious() : null);
    }

    public FilmDetailDto getById(String id) {
        SwapiDetailResponse sw = swapi.getFilmById(id);
        if (sw == null || sw.getResult() == null || sw.getResult().getProperties() == null) return null;

        SwapiProperties p = sw.getResult().getProperties();

        return new FilmDetailDto(
                sw.getResult().getUid(),
                p.getTitle(),
                p.getDirector(),
                p.getProducer(),
                p.getRelease_date()
        );
    }
}