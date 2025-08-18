package com.swapi.challenge.people;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.people.dto.PeopleDetailDto;
import com.swapi.challenge.people.dto.PeopleSummaryDto;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeopleService {

    private final SwapiClient swapi;

    public PeopleService(SwapiClient swapi) {
        this.swapi = swapi;
    }

    public PageDto<PeopleSummaryDto> list(Integer page, Integer limit, String name, String id) {
        if (id != null && !id.trim().isEmpty()) {
            PeopleDetailDto d = getById(id.trim());
            List<PeopleSummaryDto> items = (d == null)
                    ? new ArrayList<>()
                    : new ArrayList<>(Collections.singletonList(new PeopleSummaryDto(d.getId(), d.getName())));

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

        SwapiListResponse sw = swapi.listPeople(page, limit, name);
        List<SwapiListItem> src = sw != null && sw.getResults() != null ? sw.getResults() : Collections.emptyList();

        List<PeopleSummaryDto> items = src.stream()
                .map(it -> new PeopleSummaryDto(it.getUid(), it.getName()))
                .collect(Collectors.toList());

        Integer totalPages   = sw != null ? sw.getTotal_pages()   : null;
        Integer totalRecords = sw != null ? sw.getTotal_records() : null;

        return new PageDto<>(items, page, limit, totalPages, totalRecords, sw != null ? sw.getNext() : null, sw != null ? sw.getPrevious() : null);
    }

    public PeopleDetailDto getById(String id) {
        SwapiDetailResponse sw = swapi.getPeopleById(id);
        if (sw == null || sw.getResult() == null || sw.getResult().getProperties() == null) return null;

        SwapiResult r = sw.getResult();
        SwapiProperties p = r.getProperties();

        return new PeopleDetailDto(
                r.getUid(),
                p.getName(),
                p.getGender(),
                p.getHeight(),
                p.getMass(),
                p.getBirth_year()
        );
    }
}
