package com.swapi.challenge.films;

import com.swapi.challenge.api.PageDto;
import com.swapi.challenge.films.dto.FilmDetailDto;
import com.swapi.challenge.films.dto.FilmSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/films")
@SecurityRequirement(name = "bearerAuth")
public class FilmController {
    private final FilmService service;
    public FilmController(FilmService service) { this.service = service; }

    @Operation(summary = "Paginated list of Film (optional filter by 'name' or 'title')")
    @GetMapping
    public ResponseEntity<PageDto<FilmSummaryDto>> list(
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "1", required=false) Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10",required=false) Integer limit,
            @Parameter(description = "Film name")
            @RequestParam(required=false) String name,
            @Parameter(description = "Film id")
            @RequestParam(required = false) String id) {
        return ResponseEntity.ok(service.list(page, limit, name, id));
    }

    @Operation(summary = "Film Detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FilmDetailDto> get(@PathVariable String id) {
        FilmDetailDto dto = service.getById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }
}
