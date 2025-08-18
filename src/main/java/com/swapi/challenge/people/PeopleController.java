package com.swapi.challenge.people;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.people.dto.PeopleDetailDto;
import com.swapi.challenge.people.dto.PeopleSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/people")
@SecurityRequirement(name = "bearerAuth")
public class PeopleController {

    private final PeopleService service;

    public PeopleController(PeopleService service) { this.service = service; }

    @Operation(summary = "Paginated list of People (optional filter by 'name')")
    @GetMapping
    public ResponseEntity<PageDto<PeopleSummaryDto>> list(
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "1", required=false) Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10",required=false) Integer limit,
            @Parameter(description = "People name")
            @RequestParam(required=false) String name,
            @Parameter(description = "People id")
            @RequestParam(required = false) String id)
    {
        return ResponseEntity.ok(service.list(page, limit, name, id));
    }

    @Operation(summary = "People Detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PeopleDetailDto> getById(@PathVariable("id") String id) {
        PeopleDetailDto dto = service.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
