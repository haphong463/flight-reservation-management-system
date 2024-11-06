package com.windev.flight_service.service;

import com.windev.flight_service.dto.FlightDTO;

import com.windev.flight_service.payload.request.CreateFlightRequest;
import com.windev.flight_service.payload.request.UpdateFlightRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

public interface FlightService {
    PaginatedResponse<FlightDTO> getAllFlights(Pageable pageable);

    FlightDTO getOneFlight(String id);

    FlightDTO createFlight(CreateFlightRequest request);

    FlightDTO updateFlight(UpdateFlightRequest flight);

    void deleteFlight(String id);
}
