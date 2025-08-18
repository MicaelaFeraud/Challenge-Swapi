package com.swapi.challenge.vehicle;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.vehicle.dto.VehicleDetailDto;
import com.swapi.challenge.vehicle.dto.VehicleSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {
    private final VehicleService service;
    public VehicleController(VehicleService service) { this.service = service; }

    @Operation(summary = "Paginated list of Vehicles (optional filter by 'name')")
    @GetMapping
    public ResponseEntity<PageDto<VehicleSummaryDto>> list(
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "1", required=false) Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10",required=false) Integer limit,
            @Parameter(description = "Vehicle name")
            @RequestParam(required=false) String name,
            @Parameter(description = "Vehicle id")
            @RequestParam(required = false) String id) {
        return ResponseEntity.ok(service.list(page, limit, name, id));
    }

    @Operation(summary = "Vehicle Detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDetailDto> get(@PathVariable String id) {
        VehicleDetailDto dto = service.getById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }
}
