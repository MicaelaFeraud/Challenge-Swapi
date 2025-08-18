package com.swapi.challenge.starships;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.starship.StarshipService;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.SwapiDetailResponse;
import com.swapi.challenge.swapi.dto.SwapiResult;
import com.swapi.challenge.swapi.dto.SwapiListItem;
import com.swapi.challenge.swapi.dto.SwapiListResponse;
import com.swapi.challenge.swapi.dto.SwapiProperties;
import com.swapi.challenge.starship.dto.StarshipDetailDto;
import com.swapi.challenge.starship.dto.StarshipSummaryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StarshipServiceTest {

    @Mock SwapiClient swapi;
    @InjectMocks
    StarshipService service;

    @Test
    void list_byName_shouldMapAndPaginate() {
        SwapiListItem it = new SwapiListItem();
        it.setUid("9");
        it.setName("Death Star");

        SwapiListResponse sw = new SwapiListResponse();
        sw.setResults(Collections.singletonList(it));
        sw.setTotal_pages(1);
        sw.setTotal_records(1);

        when(swapi.listStarships(1, 10, "death")).thenReturn(sw);

        PageDto<StarshipSummaryDto> page = service.list(1, 10, "death", null);

        assertNotNull(page);
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getTotalRecords());
        assertEquals(1, page.getItems().size());
        assertEquals("9", page.getItems().get(0).getId());
        assertEquals("Death Star", page.getItems().get(0).getName());

        verify(swapi).listStarships(1, 10, "death");
        verify(swapi, never()).getStarshipById(anyString());
    }

    @Test
    void list_byId_shouldReturnSingleItemAndBypassListCall() {
        SwapiProperties props = new SwapiProperties();
        props.setName("Death Star");

        SwapiResult result = new SwapiResult();
        result.setUid("9");
        result.setProperties(props);

        SwapiDetailResponse detail = new SwapiDetailResponse();
        detail.setResult(result);

        when(swapi.getStarshipById("9")).thenReturn(detail);

        PageDto<StarshipSummaryDto> page = service.list(7, 20, null, "9");

        assertNotNull(page);
        assertEquals(1, page.getItems().size());
        assertEquals("9", page.getItems().get(0).getId());

        verify(swapi, never()).listStarships(any(), any(), any());
        verify(swapi).getStarshipById("9");
    }

    @Test
    void getById_shouldReturnDetailWithId() {
        SwapiProperties props = new SwapiProperties();

        SwapiResult result = new SwapiResult();
        result.setUid("9");
        result.setProperties(props);

        SwapiDetailResponse detail = new SwapiDetailResponse();
        detail.setResult(result);

        when(swapi.getStarshipById("9")).thenReturn(detail);

        StarshipDetailDto dto = service.getById("9");

        assertNotNull(dto);
        assertEquals("9", dto.getId());
    }
}
