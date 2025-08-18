package com.swapi.challenge.starship;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.starship.dto.StarshipDetailDto;
import com.swapi.challenge.starship.dto.StarshipSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/starships")
@SecurityRequirement(name = "bearerAuth")
public class StarshipController {
    private final StarshipService service;
    public StarshipController(StarshipService service) { this.service = service; }

    @Operation(summary = "Paginated list of Starship (optional filter by 'name')\"")
    @GetMapping
    public ResponseEntity<PageDto<StarshipSummaryDto>> list(
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "1", required=false) Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10",required=false) Integer limit,
            @Parameter(description = "Starship name")
            @RequestParam(required=false) String name,
            @Parameter(description = "Starship id")
            @RequestParam(required = false) String id) {
        return ResponseEntity.ok(service.list(page, limit, name, id));
    }

    @Operation(summary = "Starship Detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<StarshipDetailDto> get(@PathVariable String id) {
        StarshipDetailDto dto = service.getById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }
}
