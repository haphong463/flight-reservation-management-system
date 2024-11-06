package com.windev.flight_service.service.impl;

import com.windev.flight_service.dto.FlightDTO;
import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.entity.Seat;
import com.windev.flight_service.enums.ClassType;
import com.windev.flight_service.enums.FlightStatus;
import com.windev.flight_service.mapper.FlightMapper;
import com.windev.flight_service.payload.request.CreateFlightRequest;
import com.windev.flight_service.payload.request.UpdateFlightRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.repository.FlightRepository;
import com.windev.flight_service.repository.SeatRepository;
import com.windev.flight_service.service.FlightService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    private final FlightMapper flightMapper;

    private final SeatRepository seatRepository;



    /**
     * Get pagination of flights
     *
     * @param pageable
     * @return pagination of flights
     */
    @Override
    public PaginatedResponse<FlightDTO> getAllFlights(Pageable pageable) {
        Page<Flight> flightPage
                = flightRepository.findAll(pageable);

        List<FlightDTO> list = flightPage.stream().map(flightMapper::toDTO).toList();


        return new PaginatedResponse<>(list
                , flightPage.getNumber()
                , flightPage.getSize()
                , flightPage.getTotalElements()
                , flightPage.getTotalPages()
                , flightPage.isLast());
    }

    @Override
    public FlightDTO getOneFlight(String id) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight with ID: " + id + " not found."));

        return flightMapper.toDTO(existingFlight);
    }

    @Override
    public FlightDTO createFlight(CreateFlightRequest request) {
        Flight newFlight = new Flight();
        newFlight.setId(UUID.randomUUID().toString());
        /**
         * Map từ createFlightRequest vào entity
         */
        flightMapper.createFlightFromRequest(request, newFlight);

        /**
         * Set status cho flight khi vừa tạo là: ON TIME
         */
        newFlight.setStatus(FlightStatus.ON_TIME.name());

        Flight savedFlight = flightRepository.save(newFlight);


        List<Seat> economySeats = IntStream.rangeClosed(1, request.getEconomySeats())
                .mapToObj(i -> {
                    Seat seat = new Seat();
                    seat.setSeatNumber("E" + i);
                    seat.setType(ClassType.ECONOMY.name());
                    seat.setIsAvailable(true);
                    seat.setPrice(determineSeatPrice(ClassType.ECONOMY.name()));
                    seat.setFlight(savedFlight);
                    return seat;
                })
                .collect(Collectors.toList());

        List<Seat> businessSeats = IntStream.rangeClosed(1, request.getBusinessSeats())
                .mapToObj(i -> {
                    Seat seat = new Seat();
                    seat.setSeatNumber("B" + i);
                    seat.setType(ClassType.BUSINESS.name());
                    seat.setIsAvailable(true);
                    seat.setPrice(determineSeatPrice(ClassType.BUSINESS.name()));
                    seat.setFlight(savedFlight);
                    return seat;
                })
                .collect(Collectors.toList());

        List<Seat> allSeats = new ArrayList<>();
        allSeats.addAll(economySeats);
        allSeats.addAll(businessSeats);

        seatRepository.saveAll(allSeats);

        savedFlight.setSeats(allSeats);

        return flightMapper.toDTO(savedFlight);
    }

    @Override
    public FlightDTO updateFlight(UpdateFlightRequest flight) {
        return null;
    }

    @Override
    public void deleteFlight(String id) {

    }

    private Double determineSeatPrice(String seatClass) {
        return switch (seatClass) {
            case "ECONOMY" -> 100.0;
            case "BUSINESS" -> 200.0;
            default -> 100.0;
        };
    }
}
