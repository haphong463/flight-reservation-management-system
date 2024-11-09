package com.windev.flight_service.controller;

import com.windev.flight_service.dto.CrewDTO;
import com.windev.flight_service.dto.CrewWithFlightsDTO;
import com.windev.flight_service.payload.request.crew.CrewRequest;
import com.windev.flight_service.payload.request.crew.SearchCrewRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.service.CrewService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class CrewController {

    private final CrewService crewService;

    @PostMapping
    public ResponseEntity<CrewWithFlightsDTO> createCrew(@Valid @RequestBody CrewRequest request) {
        CrewWithFlightsDTO createdCrew = crewService.createCrew(request);
        return new ResponseEntity<>(createdCrew, HttpStatus.CREATED);
    }

    @GetMapping("/{crewId}")
    public ResponseEntity<CrewWithFlightsDTO> getCrewById(@PathVariable Long crewId) {
        CrewWithFlightsDTO crew = crewService.getByCrewId(crewId);
        return ResponseEntity.ok(crew);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<CrewDTO>> getAllCrews(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PaginatedResponse<CrewDTO> response = crewService.getAllCrewsPaginated(pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{crewId}")
    public ResponseEntity<CrewWithFlightsDTO> updateCrew(
            @PathVariable Long crewId,
            @Valid @RequestBody CrewRequest request) {
        CrewWithFlightsDTO updatedCrew = crewService.updateCrew(crewId, request);
        return ResponseEntity.ok(updatedCrew);
    }

    @DeleteMapping("/{crewId}")
    public ResponseEntity<Void> deleteCrew(@PathVariable Long crewId) {
        crewService.deleteCrew(crewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<CrewDTO>> searchCrews(@ModelAttribute SearchCrewRequest request,
                                                                  @RequestParam(defaultValue = "0") int pageNumber,
                                                                  @RequestParam(defaultValue = "10") int pageSize){
        return ResponseEntity.ok().body(crewService.searchCrews(request, pageNumber, pageSize));
    }
}
