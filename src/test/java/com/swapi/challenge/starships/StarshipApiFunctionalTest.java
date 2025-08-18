package com.swapi.challenge.starships;

import com.swapi.challenge.auth.JwtService;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.SwapiDetailResponse;
import com.swapi.challenge.swapi.dto.SwapiResult;
import com.swapi.challenge.swapi.dto.SwapiListItem;
import com.swapi.challenge.swapi.dto.SwapiListResponse;
import com.swapi.challenge.swapi.dto.SwapiProperties;
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
class StarshipApiFunctionalTest {

    @Autowired MockMvc mvc;

    @MockBean SwapiClient swapi;
    @MockBean JwtService jwtService;

    @Test
    void starships_list_byName_endToEnd() throws Exception {
        SwapiListItem it = new SwapiListItem();
        it.setUid("9");
        it.setName("Death Star");

        SwapiListResponse sw = new SwapiListResponse();
        sw.setResults(Collections.singletonList(it));
        sw.setTotal_pages(1);
        sw.setTotal_records(1);

        when(swapi.listStarships(1, 10, "death")).thenReturn(sw);

        mvc.perform(get("/api/starships").param("page","1").param("limit","10").param("name","death"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].id").value("9"))
                .andExpect(jsonPath("$.items[0].name").value("Death Star"))
                .andExpect(jsonPath("$.totalRecords").value(1));
    }

    @Test
    void starships_list_byId_endToEnd() throws Exception {
        SwapiProperties p = new SwapiProperties();
        p.setName("Death Star");

        SwapiResult r = new SwapiResult();
        r.setUid("9");
        r.setProperties(p);

        SwapiDetailResponse d = new SwapiDetailResponse();
        d.setResult(r);

        when(swapi.getStarshipById("9")).thenReturn(d);

        mvc.perform(get("/api/starships").param("id","9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("9"))
                .andExpect(jsonPath("$.items[0].name").value("Death Star"));
    }
}

