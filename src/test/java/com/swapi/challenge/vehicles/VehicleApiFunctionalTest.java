package com.swapi.challenge.vehicles;

import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class VehicleApiFunctionalTest {

    @Autowired MockMvc mvc;
    @MockBean SwapiClient swapi;

    @Test
    void vehicles_list_byName_endToEnd() throws Exception {
        SwapiListItem it = new SwapiListItem(); it.setUid("14"); it.setName("Sand Crawler");
        SwapiListResponse sw = new SwapiListResponse();
        sw.setResults(Collections.singletonList(it));
        sw.setTotal_pages(1); sw.setTotal_records(1);

        when(swapi.listVehicles(1, 10, "sand")).thenReturn(sw);

        mvc.perform(get("/api/vehicles").param("page","1").param("limit","10").param("name","sand"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].id").value("14"))
                .andExpect(jsonPath("$.totalRecords").value(1));
    }

    @Test
    void vehicles_list_byId_endToEnd() throws Exception {
        SwapiProperties p = new SwapiProperties();
        p.setName("Sand Crawler");
        p.setModel("Digger Crawler");
        p.setManufacturer("Corellia");
        p.setVehicle_class("wheeled");

        SwapiResult r = new SwapiResult();
        r.setUid("14");
        r.setProperties(p);

        SwapiDetailResponse d = new SwapiDetailResponse();
        d.setResult(r);

        when(swapi.getVehicleById("14")).thenReturn(d);

        mvc.perform(get("/api/vehicles").param("id","14"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Sand Crawler"));
    }
}
