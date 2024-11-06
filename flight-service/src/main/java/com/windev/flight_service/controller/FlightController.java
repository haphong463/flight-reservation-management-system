package com.windev.flight_service.controller;

import com.windev.flight_service.payload.request.CreateFlightRequest;
import com.windev.flight_service.service.FlightService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<?> createFlight(@Valid @RequestBody CreateFlightRequest request){
        try {
            return new ResponseEntity<>(flightService.createFlight(request), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFlights(Pageable pageable){
        try {
            return new ResponseEntity<>(flightService.getAllFlights(pageable), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
