package com.windev.flight_service.service;

import com.windev.flight_service.dto.CrewDTO;
import com.windev.flight_service.dto.FlightDTO;

import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.dto.SeatDTO;
import com.windev.flight_service.payload.request.flight.CreateFlightRequest;
import com.windev.flight_service.payload.request.flight.UpdateFlightRequest;
import com.windev.flight_service.payload.request.flight.UpdateFlightStatusRequest;
import com.windev.flight_service.payload.request.seat.UpdateSeatRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import java.util.Date;
import java.util.Set;

public interface FlightService {

    //                                  FLIGHT
    PaginatedResponse<FlightDTO> getAllFlights(int pageNumber, int pageSize);

    FlightDetailDTO getOneFlight(String id);

    FlightDetailDTO createFlight(CreateFlightRequest request);

    FlightDetailDTO updateFlight(String id, UpdateFlightRequest flight);

    FlightDetailDTO updateFlightStatus(String id, UpdateFlightStatusRequest request);

    void deleteFlight(String id);

    PaginatedResponse<FlightDTO> searchFlights(String origin, String destination, Date departureDate, int pageNumber,
                                               int pageSize);

    //                                  SEAT
    SeatDTO updateSeat(String flightId, String seatId, UpdateSeatRequest request);

    //                                  CREW
    FlightDetailDTO assignCrewToFlight(String flightId, Set<Long> crewIds);

    FlightDetailDTO removeCrewFromFlight(String flightId, Set<Long> crewIds);

}
