package com.swapi.challenge.people;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.SwapiDetailResponse;
import com.swapi.challenge.swapi.dto.SwapiResult;
import com.swapi.challenge.swapi.dto.SwapiListItem;
import com.swapi.challenge.swapi.dto.SwapiListResponse;
import com.swapi.challenge.swapi.dto.SwapiProperties;
import com.swapi.challenge.people.dto.PeopleDetailDto;
import com.swapi.challenge.people.dto.PeopleSummaryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeopleServiceTest {

    @Mock
    SwapiClient swapi;
    @InjectMocks
    PeopleService service;

    @Test
    void list_byName_shouldMapAndPaginate() {
        SwapiListItem it = new SwapiListItem();
        it.setUid("1");
        it.setName("Luke Skywalker");

        SwapiListResponse sw = new SwapiListResponse();
        sw.setResults(Collections.singletonList(it));
        sw.setTotal_pages(1);
        sw.setTotal_records(1);

        when(swapi.listPeople(1, 10, "luke")).thenReturn(sw);

        PageDto<PeopleSummaryDto> page = service.list(1, 10, "luke", null);

        assertNotNull(page);
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getTotalRecords());
        assertEquals(1, page.getItems().size());
        assertEquals("1", page.getItems().get(0).getId());
        assertEquals("Luke Skywalker", page.getItems().get(0).getName());

        verify(swapi).listPeople(1, 10, "luke");
        verify(swapi, never()).getPeopleById(anyString());
    }

    @Test
    void list_byId_shouldReturnSingleItemAndBypassListCall() {
        SwapiProperties props = new SwapiProperties();
        props.setName("Luke Skywalker");

        SwapiResult result = new SwapiResult();
        result.setUid("1");
        result.setProperties(props);

        SwapiDetailResponse detail = new SwapiDetailResponse();
        detail.setResult(result);

        when(swapi.getPeopleById("1")).thenReturn(detail);

        PageDto<PeopleSummaryDto> page = service.list(3, 5, null, "1");

        assertNotNull(page);
        assertEquals(1, page.getItems().size());
        assertEquals("1", page.getItems().get(0).getId());

        verify(swapi, never()).listPeople(any(), any(), any());
        verify(swapi).getPeopleById("1");
    }

    @Test
    void getById_shouldReturnDetailWithId() {
        SwapiProperties props = new SwapiProperties();
        props.setName("Luke Skywalker");

        SwapiResult result = new SwapiResult();
        result.setUid("1");
        result.setProperties(props);

        SwapiDetailResponse detail = new SwapiDetailResponse();
        detail.setResult(result);

        when(swapi.getPeopleById("1")).thenReturn(detail);

        PeopleDetailDto dto = service.getById("1");

        assertNotNull(dto);
        assertEquals("1", dto.getId());
    }
}
