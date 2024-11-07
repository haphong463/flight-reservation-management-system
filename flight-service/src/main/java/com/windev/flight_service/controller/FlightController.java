package com.windev.flight_service.controller;

import com.windev.flight_service.dto.FlightDTO;
import com.windev.flight_service.payload.request.CreateFlightRequest;
import com.windev.flight_service.payload.request.UpdateFlightRequest;
import com.windev.flight_service.payload.request.UpdateSeatRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.service.FlightService;
import jakarta.validation.Valid;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<?> createFlight(@RequestBody @Valid CreateFlightRequest request) {
        try {
            return new ResponseEntity<>(flightService.createFlight(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFlights(@RequestParam(defaultValue = "0") int pageNumber
                                        , @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginatedResponse<FlightDTO> flights = flightService.getAllFlights(pageNumber, pageSize);
            return new ResponseEntity<>(flights, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(@RequestParam String origin
            , @RequestParam String destination
            , @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date departureDate
            , @RequestParam(defaultValue = "0") int pageNumber
            , @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginatedResponse<FlightDTO> flights = flightService.searchFlights(origin, destination, departureDate,
                    pageNumber, pageSize);
            return new ResponseEntity<>(flights, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOneFlight(@PathVariable String id) {
        try {
            return new ResponseEntity<>(flightService.getOneFlight(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteFlight(@PathVariable String id) {
        try {
            flightService.deleteFlight(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateFlight(@PathVariable String id, @RequestBody @Valid UpdateFlightRequest request) {
        try {
            return new ResponseEntity<>(flightService.updateFlight(id, request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{flightId}/seat/{seatId}")
    public ResponseEntity<?> updateSeat(@PathVariable String flightId, @PathVariable String seatId, @RequestBody @Valid UpdateSeatRequest request) {
        try {
            return new ResponseEntity<>(flightService.updateSeat(flightId, seatId, request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
