package com.swapi.challenge.vehicles;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.vehicle.VehicleController;
import com.swapi.challenge.vehicle.VehicleService;
import com.swapi.challenge.vehicle.dto.VehicleSummaryDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VehicleController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.swapi.challenge.auth.JwtAuthFilter.class))
@Import(VehicleControllerTest.SecurityPermitAllTestConfig.class)
class VehicleControllerTest {

    @Autowired MockMvc mvc;
    @MockBean
    VehicleService service;

    @TestConfiguration
    static class SecurityPermitAllTestConfig {
        @Bean
        SecurityFilterChain security(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void list_byName_shouldReturn200AndPage() throws Exception {
        PageDto<VehicleSummaryDto> page = new PageDto<>(
                Collections.singletonList(new VehicleSummaryDto("14", "Sand Crawler")),
                1, 10, 1, 1, null, null
        );

        Mockito.when(service.list(eq(1), eq(10), eq("sand"), isNull()))
                .thenReturn(page);

        mvc.perform(get("/api/vehicles")
                        .param("page","1").param("limit","10").param("name","sand"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items[0].id").value("14"))
                .andExpect(jsonPath("$.items[0].name").value("Sand Crawler"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10));
    }

    @Test
    void list_byId_shouldReturnSingleItem() throws Exception {
        PageDto<VehicleSummaryDto> page = new PageDto<>(
                Collections.singletonList(new VehicleSummaryDto("14", "Sand Crawler")),
                1, 10, 1, 1, null, null
        );

        // Si tu controller nuevo acepta ?id=..., usá esta firma:
        Mockito.when(service.list(any(), any(), isNull(), eq("14"))).thenReturn(page);

        mvc.perform(get("/api/vehicles").param("id","14"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("14"));
    }

    @Test
    void get_detail_shouldReturn404WhenNotFound() throws Exception {
        Mockito.when(service.getById("999")).thenReturn(null);

        mvc.perform(get("/api/vehicles/999"))
                .andExpect(status().isNotFound());
    }
}

