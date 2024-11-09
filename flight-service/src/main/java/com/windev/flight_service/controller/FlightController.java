package com.windev.flight_service.controller;

import com.windev.flight_service.dto.FlightDTO;
import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.dto.SeatDTO;
import com.windev.flight_service.payload.request.flight.CreateFlightRequest;
import com.windev.flight_service.payload.request.flight.UpdateFlightRequest;
import com.windev.flight_service.payload.request.seat.UpdateSeatRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.service.FlightService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/flights")
@Tag(name = "Flight Management", description = "APIs for managing flights")
public class FlightController {

    private final FlightService flightService;

    @Operation(summary = "Create a new flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight created successfully",
                    content = @Content(schema = @Schema(implementation = FlightDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<FlightDetailDTO> createFlight(@RequestBody @Valid CreateFlightRequest request) {
        return new ResponseEntity<>(flightService.createFlight(request), HttpStatus.CREATED);

    }

    @Operation(summary = "Get all flights with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of flights",
                    content = @Content(schema = @Schema(implementation = PaginatedResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<FlightDTO>> getAllFlights(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        PaginatedResponse<FlightDTO> flights = flightService.getAllFlights(pageNumber, pageSize);
        return new ResponseEntity<>(flights, HttpStatus.OK);

    }

    @Operation(summary = "Search for flights based on origin, destination, and departure date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of flights matching search criteria",
                    content = @Content(schema = @Schema(implementation = PaginatedResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<FlightDTO>> searchFlights(
            @Parameter(description = "Origin airport code") @RequestParam String origin,
            @Parameter(description = "Destination airport code") @RequestParam String destination,
            @Parameter(description = "Departure date in ISO format") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date departureDate,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        PaginatedResponse<FlightDTO> flights = flightService.searchFlights(origin, destination, departureDate,
                pageNumber, pageSize);
        return new ResponseEntity<>(flights, HttpStatus.OK);

    }

    @Operation(summary = "Get flight details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight details",
                    content = @Content(schema = @Schema(implementation = FlightDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("{id}")
    public ResponseEntity<?> getOneFlight(@PathVariable String id) {
        try {
            return new ResponseEntity<>(flightService.getOneFlight(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete a flight by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flight deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteFlight(@PathVariable String id) {
        flightService.deleteFlight(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Update flight details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight updated successfully",
                    content = @Content(schema = @Schema(implementation = FlightDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("{id}")
    public ResponseEntity<FlightDetailDTO> updateFlight(@PathVariable String id,
                                                        @RequestBody @Valid UpdateFlightRequest request) {
        return new ResponseEntity<>(flightService.updateFlight(id, request), HttpStatus.OK);
    }

    @Operation(summary = "Update seat information for a flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seat updated successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{flightId}/seat/{seatId}")
    public ResponseEntity<SeatDTO> updateSeat(
            @PathVariable String flightId,
            @PathVariable String seatId,
            @RequestBody @Valid UpdateSeatRequest request) {

        return new ResponseEntity<>(flightService.updateSeat(flightId, seatId, request), HttpStatus.OK);

    }

    @PostMapping("/assign/{flightId}")
    public ResponseEntity<FlightDetailDTO> assignCrewsToFlight(@PathVariable String flightId,
                                                               @RequestParam Set<Long> crewIds){
        FlightDetailDTO flightDTO = flightService.assignCrewToFlight(flightId, crewIds);
        return ResponseEntity.ok(flightDTO);
    }

    @PostMapping("/remove/{flightId}")
    public ResponseEntity<FlightDetailDTO> removeCrewsFromFlight(@PathVariable String flightId,
                                                         @RequestParam Set<Long> crewIds){
        FlightDetailDTO flightDTO = flightService.removeCrewFromFlight(flightId, crewIds);
        return ResponseEntity.ok(flightDTO);
    }
}
