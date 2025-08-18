package com.swapi.challenge.vehicle;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.swapi.SwapiClient;
import com.swapi.challenge.swapi.dto.SwapiDetailResponse;
import com.swapi.challenge.swapi.dto.SwapiListItem;
import com.swapi.challenge.swapi.dto.SwapiListResponse;
import com.swapi.challenge.swapi.dto.SwapiProperties;
import com.swapi.challenge.vehicle.dto.VehicleDetailDto;
import com.swapi.challenge.vehicle.dto.VehicleSummaryDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final SwapiClient swapi;

    public VehicleService(SwapiClient swapi) {
        this.swapi = swapi;
    }

    public PageDto<VehicleSummaryDto> list(Integer page, Integer limit, String name, String id) {
        if (id != null && !id.trim().isEmpty()) {
            VehicleDetailDto d = getById(id.trim());
            List<VehicleSummaryDto> items = (d == null)
                    ? new ArrayList<>()
                    : new ArrayList<>(Collections.singletonList(new VehicleSummaryDto(d.getId(), d.getName())));

            int total = items.size();
            int l = (limit == null || limit < 1) ? 10 : limit;
            Integer totalPages = (total == 0) ? 0 : 1;

            return new PageDto<>(
                    items,
                    1,
                    l,
                    totalPages,
                    total,
                    null,
                    null
            );
        }

        SwapiListResponse sw = swapi.listVehicles(page, limit, name);

        List<VehicleSummaryDto> items = (sw == null || sw.getResults() == null)
                ? Collections.emptyList()
                : sw.getResults().stream()
                .map(it -> new VehicleSummaryDto(it.getUid(), it.getName()))
                .collect(Collectors.toList());

        Integer totalPages   = (sw != null) ? sw.getTotal_pages()   : null;
        Integer totalRecords = (sw != null) ? sw.getTotal_records() : null;

        return new PageDto<>(
                items,
                page,
                limit,
                totalPages,
                totalRecords,
                (sw != null) ? sw.getNext() : null,
                (sw != null) ? sw.getPrevious() : null
        );
    }

    public VehicleDetailDto getById(String id) {
        SwapiDetailResponse sw = swapi.getVehicleById(id);
        if (sw == null || sw.getResult() == null || sw.getResult().getProperties() == null) return null;

        SwapiProperties p = sw.getResult().getProperties();

        return new VehicleDetailDto(
                sw.getResult().getUid(),
                p.getName(),
                p.getModel(),
                p.getManufacturer(),
                p.getVehicle_class()
        );
    }
}

