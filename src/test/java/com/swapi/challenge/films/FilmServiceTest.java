package com.swapi.challenge.films;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.SwapiDetailResponse;
import com.swapi.challenge.swapi.dto.SwapiResult;
import com.swapi.challenge.swapi.dto.SwapiListItem;
import com.swapi.challenge.swapi.dto.SwapiListResponse;
import com.swapi.challenge.swapi.dto.SwapiProperties;
import com.swapi.challenge.films.dto.FilmDetailDto;
import com.swapi.challenge.films.dto.FilmSummaryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock SwapiClient swapi;
    @InjectMocks
    FilmService service;

    @Test
    void list_byName_shouldMapAndPaginate() {
        SwapiListItem it = new SwapiListItem();
        it.setUid("1");
        it.setName("A New Hope"); // si tu SwapiListItem usa 'name' para films

        SwapiListResponse sw = new SwapiListResponse();
        sw.setResults(Collections.singletonList(it));
        sw.setTotal_pages(1);
        sw.setTotal_records(1);

        when(swapi.listFilms(1, 10, "hope")).thenReturn(sw);

        PageDto<FilmSummaryDto> page = service.list(1, 10, "hope", null);

        assertNotNull(page);
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getTotalRecords());
        assertEquals(1, page.getItems().size());
        assertEquals("1", page.getItems().get(0).getId());
        assertEquals("A New Hope", page.getItems().get(0).getTitle());

        verify(swapi).listFilms(1, 10, "hope");
        verify(swapi, never()).getFilmById(anyString());
    }

    @Test
    void list_byId_shouldReturnSingleItemAndBypassListCall() {
        SwapiProperties props = new SwapiProperties();

        SwapiResult result = new SwapiResult();
        result.setUid("1");
        result.setProperties(props);

        SwapiDetailResponse detail = new SwapiDetailResponse();
        detail.setResult(result);

        when(swapi.getFilmById("1")).thenReturn(detail);

        PageDto<FilmSummaryDto> page = service.list(2, 5, null, "1");

        assertNotNull(page);
        assertEquals(1, page.getItems().size());
        assertEquals("1", page.getItems().get(0).getId());

        verify(swapi, never()).listFilms(any(), any(), any());
        verify(swapi).getFilmById("1");
    }

    @Test
    void getById_shouldReturnDetailWithId() {
        SwapiProperties props = new SwapiProperties();

        SwapiResult result = new SwapiResult();
        result.setUid("1");
        result.setProperties(props);

        SwapiDetailResponse detail = new SwapiDetailResponse();
        detail.setResult(result);

        when(swapi.getFilmById("1")).thenReturn(detail);

        FilmDetailDto dto = service.getById("1");

        assertNotNull(dto);
        assertEquals("1", dto.getId());
    }
}

