package com.swapi.challenge.people;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.people.dto.PeopleSummaryDto;
import com.swapi.challenge.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PeopleController.class)
@AutoConfigureMockMvc(addFilters = false)
class PeopleControllerTest {

    @Autowired MockMvc mvc;

    @MockBean
    PeopleService service;

    // Mock para que el contexto levante el filtro sin romper
    @MockBean JwtService jwtService;

    @Test
    void list_byName_shouldReturn200AndPage() throws Exception {
        PageDto<PeopleSummaryDto> page = new PageDto<>(
                Collections.singletonList(new PeopleSummaryDto("1", "Luke Skywalker")),
                1, 10, 1, 1, null, null
        );

        Mockito.when(service.list(eq(1), eq(10), eq("sky"), isNull()))
                .thenReturn(page);

        mvc.perform(get("/api/people")
                        .param("page","1").param("limit","10").param("name","sky"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].id").value("1"))
                .andExpect(jsonPath("$.items[0].name").value("Luke Skywalker"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10));
    }

    @Test
    void list_byId_shouldReturnSingleItem() throws Exception {
        PageDto<PeopleSummaryDto> page = new PageDto<>(
                Collections.singletonList(new PeopleSummaryDto("1", "Luke Skywalker")),
                1, 10, 1, 1, null, null
        );

        Mockito.when(service.list(any(), any(), isNull(), eq("1")))
                .thenReturn(page);

        mvc.perform(get("/api/people").param("id","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("1"));
    }
}
