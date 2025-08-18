package com.swapi.challenge.vehicles;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.*;
import com.swapi.challenge.vehicle.VehicleService;
import com.swapi.challenge.vehicle.dto.VehicleDetailDto;
import com.swapi.challenge.vehicle.dto.VehicleSummaryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock SwapiClient swapi;
    @InjectMocks
    VehicleService service;

    @Test
    void list_byName_shouldMapAndPaginate() {
        SwapiListItem it = new SwapiListItem();
        it.setUid("14");
        it.setName("Sand Crawler");

        SwapiListResponse sw = new SwapiListResponse();
        sw.setResults(Collections.singletonList(it));
        sw.setTotal_pages(1);
        sw.setTotal_records(1);
        sw.setNext(null);
        sw.setPrevious(null);

        when(swapi.listVehicles(1, 10, "sand")).thenReturn(sw);

        PageDto<VehicleSummaryDto> page = service.list(1, 10, "sand" /*name*/, null /*id*/);

        assertNotNull(page);
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getTotalRecords());
        assertEquals(1, page.getItems().size());
        assertEquals("14", page.getItems().get(0).getId());
        assertEquals("Sand Crawler", page.getItems().get(0).getName());

        verify(swapi).listVehicles(1, 10, "sand");
        verify(swapi, never()).getVehicleById(anyString());
    }

    @Test
    void list_byId_shouldReturnSingleItemAndBypassListCall() {
        SwapiProperties props = new SwapiProperties();
        props.setName("Sand Crawler");
        props.setModel("Digger Crawler");
        props.setManufacturer("Corellia Mining Corporation");
        props.setVehicle_class("wheeled");

        SwapiResult result = new SwapiResult();
        result.setUid("14");
        result.setProperties(props);

        SwapiDetailResponse detail = new SwapiDetailResponse();
        detail.setResult(result);

        when(swapi.getVehicleById("14")).thenReturn(detail);

        PageDto<VehicleSummaryDto> page = service.list(99, 5, null /*name*/, "14" /*id*/);

        assertNotNull(page);
        assertEquals(1, page.getPage());
        assertEquals(1, page.getItems().size());
        assertEquals("14", page.getItems().get(0).getId());
        assertEquals("Sand Crawler", page.getItems().get(0).getName());
        assertTrue(page.getTotalPages() == 0 || page.getTotalPages() == 1);

        verify(swapi, never()).listVehicles(any(), any(), any());
        verify(swapi).getVehicleById("14");
    }

    @Test
    void getById_shouldMapDetailDto() {
        SwapiProperties props = new SwapiProperties();
        props.setName("Sand Crawler");
        props.setModel("Digger Crawler");
        props.setManufacturer("Corellia");
        props.setVehicle_class("wheeled");

        SwapiResult result = new SwapiResult();
        result.setUid("14");
        result.setProperties(props);

        SwapiDetailResponse detail = new SwapiDetailResponse();
        detail.setResult(result);

        when(swapi.getVehicleById("14")).thenReturn(detail);

        VehicleDetailDto dto = service.getById("14");

        assertNotNull(dto);
        assertEquals("14", dto.getId());
        assertEquals("Sand Crawler", dto.getName());
        assertEquals("Digger Crawler", dto.getModel());
        assertEquals("Corellia", dto.getManufacturer());
        assertEquals("wheeled", dto.getVehicleClass());
    }
}