package com.swapi.challenge.starship;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.starship.dto.StarshipDetailDto;
import com.swapi.challenge.starship.dto.StarshipSummaryDto;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.SwapiDetailResponse;
import com.swapi.challenge.swapi.dto.SwapiListItem;
import com.swapi.challenge.swapi.dto.SwapiListResponse;
import com.swapi.challenge.swapi.dto.SwapiProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StarshipService {

    private final SwapiClient swapi;

    public StarshipService(SwapiClient swapi) {
        this.swapi = swapi;
    }

    public PageDto<StarshipSummaryDto> list(Integer page, Integer limit, String name, String id) {
        if (id != null && !id.trim().isEmpty()) {
            StarshipDetailDto d = getById(id.trim());
            List<StarshipSummaryDto> items = (d == null)
                    ? new ArrayList<>()
                    : new ArrayList<>(Collections.singletonList(new StarshipSummaryDto(d.getId(), d.getName())));

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
        SwapiListResponse sw = swapi.listStarships(page, limit, name);
        List<SwapiListItem> src = sw != null && sw.getResults() != null ? sw.getResults() : Collections.emptyList();

        List<StarshipSummaryDto> items = src.stream()
                .map(it -> new StarshipSummaryDto(it.getUid(), it.getName()))
                .collect(Collectors.toList());

        Integer totalPages   = sw != null ? sw.getTotal_pages()   : null;
        Integer totalRecords = sw != null ? sw.getTotal_records() : null;

        return new PageDto<>(items, page, limit, totalPages, totalRecords, sw != null ? sw.getNext() : null, sw != null ? sw.getPrevious() : null);
    }

    public StarshipDetailDto getById(String id) {
        SwapiDetailResponse sw = swapi.getStarshipById(id);
        if (sw == null || sw.getResult() == null || sw.getResult().getProperties() == null) return null;

        SwapiProperties p = sw.getResult().getProperties();

        return new StarshipDetailDto(
                sw.getResult().getUid(),
                p.getName(),
                p.getModel(),
                p.getManufacturer(),
                p.getStarship_class()
        );
    }
}

