package com.windev.flight_service.service.impl;

import com.windev.flight_service.dto.CrewWithFlightsDTO;
import com.windev.flight_service.dto.FlightDTO;
import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.dto.SeatDTO;
import com.windev.flight_service.entity.*;
import com.windev.flight_service.enums.ClassType;
import com.windev.flight_service.enums.EventType;
import com.windev.flight_service.enums.FlightStatus;
import com.windev.flight_service.exception.*;
import com.windev.flight_service.mapper.CrewMapper;
import com.windev.flight_service.mapper.FlightMapper;
import com.windev.flight_service.mapper.SeatMapper;
import com.windev.flight_service.payload.request.flight.CreateFlightRequest;
import com.windev.flight_service.payload.request.flight.UpdateFlightRequest;
import com.windev.flight_service.payload.request.flight.UpdateFlightStatusRequest;
import com.windev.flight_service.payload.request.seat.UpdateSeatRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.repository.AirplaneRepository;
import com.windev.flight_service.repository.CrewRepository;
import com.windev.flight_service.repository.FlightRepository;
import com.windev.flight_service.repository.SeatRepository;
import com.windev.flight_service.service.FlightService;
import com.windev.flight_service.service.cache.CrewCacheService;
import com.windev.flight_service.service.cache.FlightCacheService;
import com.windev.flight_service.service.kafka.FlightMessageQueue;
import com.windev.flight_service.util.DateUtil;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final FlightMessageQueue queue;
    private final CrewRepository crewRepository;
    private final CrewCacheService crewCacheService;
    private final CrewMapper crewMapper;
    private final AirplaneRepository airplaneRepository;

    /**
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public PaginatedResponse<FlightDTO> getAllFlights(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

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
                .orElseThrow(() -> new FlightNotFoundException("Flight with ID: " + id + " not found."));

        FlightDetailDTO result = flightMapper.toDetailDTO(existingFlight);
        flightCacheService.save(result);
        return result;
    }

    @Override
    public FlightDetailDTO createFlight(CreateFlightRequest request) {
        Airplane airplane = airplaneRepository.findById(request.getAirplaneId())
                .orElseThrow(() -> new AirplaneNotFoundException("Airplane with ID: " + request.getAirplaneId() +
                        " not found."));

        Flight newFlight = new Flight();
        newFlight.setAirplane(airplane);
        flightMapper.createFlightFromRequest(request, newFlight);


        Flight savedFlight = flightRepository.save(newFlight);

        List<Seat> allSeats = new ArrayList<>();

        for(SeatConfig config : airplane.getSeatConfigs()){
            List<Seat> seats = IntStream.rangeClosed(1,config.getSeatCount()).mapToObj(value -> {
                return Seat.builder()
                        .price(determineSeatPrice(config.getSeatClass()))
                        .type(config.getSeatClass())
                        .seatNumber(config.getSeatClass().charAt(0) + String.valueOf(value))
                        .isAvailable(true)
                        .flight(savedFlight)
                        .build();
            }).toList();
            allSeats.addAll(seats);
        }

        seatRepository.saveAll(allSeats);

        savedFlight.setSeats(allSeats);

        FlightDetailDTO result = flightMapper.toDetailDTO(savedFlight);

        flightCacheService.save(result);

        queue.sendMessage(result, EventType.NEW_FLIGHT.name());

        return result;
    }

    @Override
    @Transactional
    public FlightDetailDTO updateFlight(String id, UpdateFlightRequest request) {
        FlightDetailDTO existingFlight = flightCacheService.findById(id);

        if (existingFlight == null) {
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new FlightNotFoundException("Flight with ID: " + id + " not found."));
            existingFlight = flightMapper.toDetailDTO(flight);
        }

        Flight flightToUpdate = flightMapper.toEntity(existingFlight);

        flightMapper.updateFlightFromRequest(request, flightToUpdate);

        if (request.getStatus() != null) {
            flightToUpdate.setStatus(request.getStatus());
        }

        for (Seat seat : flightToUpdate.getSeats()) {
            seat.setFlight(flightToUpdate);
        }

        Flight updatedFlight = flightRepository.save(flightToUpdate);

        FlightDetailDTO result = flightMapper.toDetailDTO(updatedFlight);

        /**
         * UPDATE IN REDIS
         */
        flightCacheService.update(result);

        /**
         * SEND MESSAGE TO NOTIFICATION TOPIC
         */
        queue.sendMessage(result, EventType.EDIT_FLIGHT.name());

        return result;
    }

    @Override
    public FlightDetailDTO updateFlightStatus(String id, UpdateFlightStatusRequest request) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight with ID: " + id + " not found."));

        existingFlight.setStatus(request.getStatus());

        Flight savedFlight = flightRepository.save(existingFlight);

        return flightMapper.toDetailDTO(savedFlight);
    }


    @Override
    public void deleteFlight(String id) {
        FlightDetailDTO existingFlight = flightCacheService.findById(id);

        if (existingFlight == null) {
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new FlightNotFoundException("Flight with ID: " + id + " not found."));
            existingFlight = flightMapper.toDetailDTO(flight);
        }

        flightRepository.deleteById(id);

        flightCacheService.delete(id);

        queue.sendMessage(existingFlight, EventType.DELETE_FLIGHT.name());
    }

    @Override
    public FlightDetailDTO assignCrewToFlight(String flightId, Set<Long> crewIds) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException("Flight with ID: " + flightId + " not found."));

        List<Crew> crews = crewRepository.findAllByIdIn(crewIds);

        if (crews.isEmpty()) {
            throw new IllegalArgumentException("No valid crews found for the provided IDs.");
        }

        Set<Crew> newCrews = new HashSet<>();

        for (Crew crew : crews) {
            if (!flight.getCrews().contains(crew)) {
                newCrews.add(crew);
            } else {
                log.warn("Crew with ID: {} is already assigned to flight: {}", crew.getId(), flight.getFlightNumber());
            }
        }

        if (newCrews.isEmpty()) {
            log.info("All provided crews are already assigned to flight: {}", flight.getFlightNumber());
        } else {
            flight.getCrews().addAll(newCrews);
            flightRepository.save(flight);

            for (Crew crew : newCrews) {
                updateCrewCacheWithFlight(crew.getId(), flight);
            }

        }

        FlightDetailDTO result = flightMapper.toDetailDTO(flight);

        flightCacheService.update(result);

        return result;
    }


    @Override
    public FlightDetailDTO removeCrewFromFlight(String flightId, Set<Long> crewIds) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException("Flight with ID: " + flightId + " not found."));

        List<Crew> crews = crewRepository.findAllByIdIn(crewIds);

        if (crews.isEmpty()) {
            throw new IllegalArgumentException("No valid crews found for the provided IDs.");
        }

        Set<Crew> removeCrews = new HashSet<>();

        for (Crew crew : crews) {
            if (flight.getCrews().contains(crew)) {
                removeCrews.add(crew);
            } else {
                log.warn("Crew with ID: {} is already assigned to flight: {}", crew.getId(), flight.getFlightNumber());
            }
        }

        if(removeCrews.isEmpty()){
            log.info("No crews were removed from flight: {}", flight.getFlightNumber());
        }else{
            flight.getCrews().removeAll(removeCrews);
            flightRepository.save(flight);

            for(Crew crew : removeCrews){
                removeCrewCacheWithFlight(crew.getId(), flight);
            }
        }

        FlightDetailDTO result = flightMapper.toDetailDTO(flight);

        flightCacheService.update(result);

        return result;
    }


    @Override
    public PaginatedResponse<FlightDTO> searchFlights(String origin, String destination, Date departureDate,
                                                      int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Date startOfDay = DateUtil.getStartOfDay(departureDate);
        Date endOfDay = DateUtil.getEndOfDay(departureDate);

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
//        Flight existingFlight = flightRepository.findById(flightId)
//                .orElseThrow(() -> new FlightNotFoundException("Flight with ID: " + flightId + " not found."));
//
//        Seat existingSeat = seatRepository.findById(seatId)
//                .orElseThrow(() -> new SeatNotFoundException("Seat with ID: " + seatId + " not found."));
//
//        if (!existingSeat.getFlight().getId().equals(flightId)) {
//            throw new SeatNotBelongToFlightException("Seat with ID: " + seatId + " does not belong to Flight with ID: " + flightId);
//        }
//
//        if (request.getIsAvailable() != null) {
//            existingSeat.setIsAvailable(request.getIsAvailable());
//        }
//        if (request.getPrice() != null) {
//            existingSeat.setPrice(request.getPrice());
//        }
//        if (request.getType() != null) {
//            existingSeat.setType(request.getType());
//        }
//
//        seatRepository.save(existingSeat);
//
//        return seatMapper.toDTO(existingSeat);
        return null;
    }

    private Double determineSeatPrice(String seatClass) {
        return switch (seatClass) {
            case "ECONOMY" -> 100.0;
            case "BUSINESS" -> 200.0;
            case "FIRST" -> 300.0;
            default -> 100.0;
        };
    }

    private void updateCrewCacheWithFlight(Long crewId, Flight flight) {
        CrewWithFlightsDTO crew = getCrewWithFlightsById(crewId);

        FlightDTO flightDTO = flightMapper.toDTO(flight);

        if (!crew.getFlights().contains(flightDTO)) {
            crew.getFlights().add(flightDTO);
            crewCacheService.update(crew);
            log.info("Update cache for Crew ID: {} with Flight ID: {} ", crewId, flight.getId());
        }
    }

    private void removeCrewCacheWithFlight(Long crewId, Flight flight) {
        CrewWithFlightsDTO crew = getCrewWithFlightsById(crewId);

        FlightDTO flightDTO = flightMapper.toDTO(flight);
        if (crew.getFlights().contains(flightDTO)) {
            crew.getFlights().remove(flightDTO);
            crewCacheService.update(crew);
            log.info("Update cache for Crew ID: {} with Flight ID: {} ", crewId, flight.getId());
        }else{
            log.warn("Flight ID: {} not found in Crew ID: {} assignedFlights.", flight.getFlightNumber(), crewId);
        }
    }

    private CrewWithFlightsDTO getCrewWithFlightsById(Long crewId) {
        CrewWithFlightsDTO crew = crewCacheService.findById(String.valueOf(crewId));

        if (crew == null) {
            log.warn("Crew with ID: {} not found in cache. Fetching from repository.", crewId);
            Crew existingCrew = crewRepository.findById(crewId)
                    .orElseThrow(() -> new CrewNotFoundException("Crew with ID: " + crewId + " not found."));
            crew = crewMapper.withFlightsDTO(existingCrew);
        }
        return crew;
    }
}
