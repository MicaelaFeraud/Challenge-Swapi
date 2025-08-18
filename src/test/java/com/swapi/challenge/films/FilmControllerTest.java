package com.swapi.challenge.films;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.films.dto.FilmSummaryDto;
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

@WebMvcTest(FilmController.class) // si tu clase se llama FilmsController, cambialo
@AutoConfigureMockMvc(addFilters = false)
class FilmControllerTest {

    @Autowired MockMvc mvc;

    @MockBean
    FilmService service;

    @MockBean JwtService jwtService;

    @Test
    void list_byName_shouldReturn200AndPage() throws Exception {
        PageDto<FilmSummaryDto> page = new PageDto<>(
                Collections.singletonList(new FilmSummaryDto("1", "A New Hope")),
                1, 10, 1, 1, null, null
        );

        Mockito.when(service.list(eq(1), eq(10), eq("hope"), isNull()))
                .thenReturn(page);

        mvc.perform(get("/api/films")
                        .param("page","1").param("limit","10").param("name","hope"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].id").value("1"))
                .andExpect(jsonPath("$.items[0].title").value("A New Hope"));
    }

    @Test
    void list_byId_shouldReturnSingleItem() throws Exception {
        PageDto<FilmSummaryDto> page = new PageDto<>(
                Collections.singletonList(new FilmSummaryDto("1", "A New Hope")),
                1, 10, 1, 1, null, null
        );

        Mockito.when(service.list(any(), any(), isNull(), eq("1")))
                .thenReturn(page);

        mvc.perform(get("/api/films").param("id","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("1"));
    }
}
