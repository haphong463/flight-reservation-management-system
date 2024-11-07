package com.windev.flight_service.controller;

import com.windev.flight_service.dto.CrewDTO;
import com.windev.flight_service.payload.request.crew.CrewRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.service.CrewService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/crews")
@Slf4j
@RequiredArgsConstructor
public class CrewController {

    private final CrewService crewService;

    /**
     * Create a new crew member
     *
     * @param request The crew member details
     * @return The created CrewDTO
     */
    @PostMapping
    public ResponseEntity<CrewDTO> createCrew(@Valid @RequestBody CrewRequest request) {
        CrewDTO createdCrew = crewService.createCrew(request);
        return new ResponseEntity<>(createdCrew, HttpStatus.CREATED);
    }

    /**
     * Get a crew member by ID
     *
     * @param crewId The ID of the crew member
     * @return The CrewDTO
     */
    @GetMapping("/{crewId}")
    public ResponseEntity<CrewDTO> getCrewById(@PathVariable Long crewId) {
        CrewDTO crew = crewService.getByCrewId(crewId);
        return ResponseEntity.ok(crew);
    }

    /**
     * Get all crew members with pagination
     *
     * @param pageNumber The page number (0-based)
     * @param pageSize The size of the page
     * @return A paginated response containing CrewDTOs
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<CrewDTO>> getAllCrews(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PaginatedResponse<CrewDTO> response = crewService.getAllCrewsPaginated(pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing crew member
     *
     * @param crewId The ID of the crew member to update
     * @param request The updated crew member details
     * @return The updated CrewDTO
     */
    @PutMapping("/{crewId}")
    public ResponseEntity<CrewDTO> updateCrew(
            @PathVariable Long crewId,
            @Valid @RequestBody CrewRequest request) {
        CrewDTO updatedCrew = crewService.updateCrew(crewId, request);
        return ResponseEntity.ok(updatedCrew);
    }

    /**
     * Delete a crew member by ID
     *
     * @param crewId The ID of the crew member to delete
     * @return A response with no content
     */
    @DeleteMapping("/{crewId}")
    public ResponseEntity<Void> deleteCrew(@PathVariable Long crewId) {
        crewService.deleteCrew(crewId);
        return ResponseEntity.noContent().build();
    }

}
