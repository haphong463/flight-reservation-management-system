package com.windev.flight_service.service;

import com.windev.flight_service.dto.FlightDTO;

import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.dto.SeatDTO;
import com.windev.flight_service.payload.request.CreateFlightRequest;
import com.windev.flight_service.payload.request.UpdateFlightRequest;
import com.windev.flight_service.payload.request.UpdateSeatRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import java.util.Date;
import org.springframework.data.domain.Pageable;

public interface FlightService {

    //                                  FLIGHT
    PaginatedResponse<FlightDTO> getAllFlights(int pageNumber, int pageSize);

    FlightDetailDTO getOneFlight(String id);

    FlightDetailDTO createFlight(CreateFlightRequest request);

    FlightDetailDTO updateFlight(String id, UpdateFlightRequest flight);

    void deleteFlight(String id);

    PaginatedResponse<FlightDTO> searchFlights(String origin, String destination, Date departureDate, int pageNumber,
                                               int pageSize);

    //                                  SEAT
    SeatDTO updateSeat(String flightId, String seatId, UpdateSeatRequest request);

}
