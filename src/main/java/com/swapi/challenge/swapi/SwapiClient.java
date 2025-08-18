package com.swapi.challenge.swapi;

import com.swapi.challenge.swapi.dto.SwapiDetailResponse;
import com.swapi.challenge.swapi.dto.SwapiListItem;
import com.swapi.challenge.swapi.dto.SwapiListResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class SwapiClient {
    private final RestTemplate restTemplate;
    private final SwapiClientProperties props;

    public SwapiClient(RestTemplate restTemplate, SwapiClientProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    private SwapiListResponse listResourcePage(String resource, int page, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(props.getBaseUrl() + "/" + resource)
                .queryParam("page", page)
                .queryParam("limit", limit)
                .toUriString();

        ResponseEntity<SwapiListResponse> resp =
                restTemplate.exchange(url, HttpMethod.GET, null, SwapiListResponse.class);
        return resp.getBody();
    }

    private List<SwapiListItem> listAll(String resource, int bulkLimit) {
        List<SwapiListItem> all = new ArrayList<>();
        String nextUrl = UriComponentsBuilder.fromHttpUrl(props.getBaseUrl() + "/" + resource)
                .queryParam("page", 1)
                .queryParam("limit", bulkLimit < 1 ? props.getBulkLimit() : bulkLimit)
                .toUriString();

        while (nextUrl != null) {
            ResponseEntity<SwapiListResponse> resp =
                    restTemplate.exchange(nextUrl, HttpMethod.GET, null, SwapiListResponse.class);
            SwapiListResponse body = resp.getBody();
            if (body != null && body.getResults() != null) {
                all.addAll(body.getResults());
            }
            nextUrl = (body != null ? body.getNext() : null);
        }
        return all;
    }

    private SwapiDetailResponse getResourceById(String resource, String id) {
        ResponseEntity<SwapiDetailResponse> resp =
                restTemplate.exchange(props.getBaseUrl() + "/" + resource + "/" + id,
                        HttpMethod.GET, null, SwapiDetailResponse.class);
        return resp.getBody();
    }

    private SwapiListResponse listResource(String resource, Integer page, Integer limit, String name) {
        int l = (limit == null || limit < 1) ? props.getDefaultPageSize() : limit;

        if (name != null && !name.trim().isEmpty()) {
            String q = normalize(name);

            List<SwapiListItem> all = listAll(resource, props.getBulkLimit());
            List<SwapiListItem> filtered = all.stream()
                    .filter(item -> item.getName() != null && normalize(item.getName()).contains(q))
                    .collect(Collectors.toList());

            int total = filtered.size();
            int totalPages = (total == 0) ? 0 : (int) Math.ceil((double) total / l);

            int p = (page == null || page < 1) ? 1 : page;
            if (totalPages == 0) p = 1;
            else if (p > totalPages) p = totalPages;

            int fromIndex = (total == 0) ? 0 : Math.max(0, (p - 1) * l);
            int toIndex   = (total == 0) ? 0 : Math.min(fromIndex + l, total);
            List<SwapiListItem> pageContent = (fromIndex <= toIndex) ? filtered.subList(fromIndex, toIndex) : Collections.emptyList();

            SwapiListResponse resp = new SwapiListResponse();
            resp.setResults(pageContent);
            resp.setTotal_records(total);
            resp.setTotal_pages(totalPages);
            resp.setMessage("ok");
            return resp;
        } else {
            int p = (page == null || page < 1) ? 1 : page;
            return listResourcePage(resource, p, l);
        }
    }

    // Helper para normalizar strings (minúsculas, sin acentos y con espacios colapsados)
    private static String normalize(String s) {
        String lower = s.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        String noAccents = java.text.Normalizer.normalize(lower, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return noAccents;
    }


    public SwapiListResponse listPeople(Integer page, Integer limit, String name) {
        return listResource("people", page, limit, name);
    }

    public SwapiDetailResponse getPeopleById(String id) {
        return getResourceById("people", id);
    }

    public SwapiListResponse listFilms(Integer page, Integer limit, String name) {
        return listResource("films", page, limit, name);
    }

    public SwapiDetailResponse getFilmById(String id) {
        return getResourceById("films", id);
    }

    public SwapiListResponse listStarships(Integer page, Integer limit, String name) {
        return listResource("starships", page, limit, name);
    }

    public SwapiDetailResponse getStarshipById(String id) {
        return getResourceById("starships", id);
    }

    public SwapiListResponse listVehicles(Integer page, Integer limit, String name) {
        return listResource("vehicles", page, limit, name);
    }

    public SwapiDetailResponse getVehicleById(String id) {
        return getResourceById("vehicles", id);
    }
}
