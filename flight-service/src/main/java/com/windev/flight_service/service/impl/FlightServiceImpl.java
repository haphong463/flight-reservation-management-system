package com.windev.flight_service.service.impl;

import com.windev.flight_service.dto.FlightDTO;
import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.dto.SeatDTO;
import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.entity.Seat;
import com.windev.flight_service.enums.ClassType;
import com.windev.flight_service.enums.FlightStatus;
import com.windev.flight_service.mapper.FlightMapper;
import com.windev.flight_service.mapper.SeatMapper;
import com.windev.flight_service.payload.request.CreateFlightRequest;
import com.windev.flight_service.payload.request.UpdateFlightRequest;
import com.windev.flight_service.payload.request.UpdateSeatRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.repository.FlightRepository;
import com.windev.flight_service.repository.SeatRepository;
import com.windev.flight_service.service.FlightService;
import com.windev.flight_service.service.cache.FlightCacheService;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    private final FlightMapper flightMapper;

    private final SeatRepository seatRepository;

    private final SeatMapper seatMapper;

    private final FlightCacheService flightCacheService;


    /**
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public PaginatedResponse<FlightDTO> getAllFlights(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);

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
    public FlightDetailDTO getOneFlight(String id) {

        FlightDetailDTO flightInCached = flightCacheService.findById(id);

        if (flightInCached != null) {
            return flightInCached;
        }

        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight with ID: " + id + " not found."));

        FlightDetailDTO result = flightMapper.toDetailDTO(existingFlight);
        flightCacheService.save(result);
        return result;
    }

    @Override
    public FlightDetailDTO createFlight(CreateFlightRequest request) {
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
                .toList();

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
                .toList();

        List<Seat> firstSeats = IntStream.rangeClosed(1, request.getFirstSeats())
                .mapToObj(i -> {
                    Seat seat = new Seat();
                    seat.setSeatNumber("F" + i);
                    seat.setType(ClassType.FIRST.name());
                    seat.setIsAvailable(true);
                    seat.setPrice(determineSeatPrice(ClassType.FIRST.name()));
                    seat.setFlight(savedFlight);
                    return seat;
                })
                .toList();

        List<Seat> allSeats = new ArrayList<>();
        allSeats.addAll(economySeats);
        allSeats.addAll(businessSeats);
        allSeats.addAll(firstSeats);

        seatRepository.saveAll(allSeats);

        savedFlight.setSeats(allSeats);

        FlightDetailDTO result = flightMapper.toDetailDTO(savedFlight);

        flightCacheService.save(result);

        return result;
    }

    @Override
    @Transactional
    public FlightDetailDTO updateFlight(String id, UpdateFlightRequest request) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight with ID: " + id + " not found."));


        flightMapper.updateFlightFromRequest(request, existingFlight);

        if (request.getStatus() != null) {
            existingFlight.setStatus(request.getStatus());
        }

        Flight updatedFlight = flightRepository.save(existingFlight);
        return flightMapper.toDetailDTO(updatedFlight);
    }


    @Override
    public void deleteFlight(String id) {
        Flight existingFlight =
                flightRepository.findById(id).orElseThrow(() -> new RuntimeException("Flight with ID: " + id + " not found."));

        flightRepository.delete(existingFlight);
    }


    @Override
    public PaginatedResponse<FlightDTO> searchFlights(String origin, String destination, Date departureDate,
                                                      int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Date startOfDay = getStartOfDay(departureDate);
        Date endOfDay = getEndOfDay(departureDate);

        Page<Flight> flightPage = flightRepository.findByOriginAndDestinationAndDepartureTimeBetween(origin, destination, startOfDay, endOfDay, pageable);
        List<FlightDTO> list = flightPage.stream().map(flightMapper::toDTO).collect(Collectors.toList());

        return new PaginatedResponse<>(list,
                flightPage.getNumber(),
                flightPage.getSize(),
                flightPage.getTotalElements(),
                flightPage.getTotalPages(),
                flightPage.isLast());
    }


    @Override
    @Transactional
    public SeatDTO updateSeat(String flightId, String seatId, UpdateSeatRequest request) {
        Flight existingFlight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight with ID: " + flightId + " not found."));

        Seat existingSeat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat with ID: " + seatId + " not found."));

        if (!existingSeat.getFlight().getId().equals(flightId)) {
            throw new RuntimeException("Seat with ID: " + seatId + " does not belong to Flight with ID: " + flightId);
        }

        if (request.getIsAvailable() != null) {
            existingSeat.setIsAvailable(request.getIsAvailable());
        }
        if (request.getPrice() != null) {
            existingSeat.setPrice(request.getPrice());
        }
        if (request.getType() != null) {
            existingSeat.setType(request.getType());
        }

        seatRepository.save(existingSeat);

        return seatMapper.toDTO(existingSeat);
    }

    private Double determineSeatPrice(String seatClass) {
        return switch (seatClass) {
            case "ECONOMY" -> 100.0;
            case "BUSINESS" -> 200.0;
            case "FIRST" -> 300.0;
            default -> 100.0;
        };
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}
