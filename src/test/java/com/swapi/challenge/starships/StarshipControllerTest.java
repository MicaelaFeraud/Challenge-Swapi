package com.swapi.challenge.starships;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.starship.StarshipController;
import com.swapi.challenge.starship.StarshipService;
import com.swapi.challenge.starship.dto.StarshipSummaryDto;
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

@WebMvcTest(StarshipController.class)
@AutoConfigureMockMvc(addFilters = false)
class StarshipControllerTest {

    @Autowired MockMvc mvc;

    @MockBean
    StarshipService service;

    @MockBean JwtService jwtService;

    @Test
    void list_byName_shouldReturn200AndPage() throws Exception {
        PageDto<StarshipSummaryDto> page = new PageDto<>(
                Collections.singletonList(new StarshipSummaryDto("9", "Death Star")),
                1, 10, 1, 1, null, null
        );

        Mockito.when(service.list(eq(1), eq(10), eq("death"), isNull()))
                .thenReturn(page);

        mvc.perform(get("/api/starships")
                        .param("page","1").param("limit","10").param("name","death"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].id").value("9"))
                .andExpect(jsonPath("$.items[0].name").value("Death Star"));
    }

    @Test
    void list_byId_shouldReturnSingleItem() throws Exception {
        PageDto<StarshipSummaryDto> page = new PageDto<>(
                Collections.singletonList(new StarshipSummaryDto("9", "Death Star")),
                1, 10, 1, 1, null, null
        );

        Mockito.when(service.list(any(), any(), isNull(), eq("9")))
                .thenReturn(page);

        mvc.perform(get("/api/starships").param("id","9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("9"));
    }
}

