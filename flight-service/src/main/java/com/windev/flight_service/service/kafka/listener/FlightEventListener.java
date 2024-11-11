package com.windev.flight_service.service.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windev.flight_service.dto.FlightDetailDTO;
import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.entity.Seat;
import com.windev.flight_service.event.BookingEvent;
import com.windev.flight_service.event.TicketEvent;
import com.windev.flight_service.exception.FlightNotFoundException;
import com.windev.flight_service.mapper.FlightMapper;
import com.windev.flight_service.payload.response.EventMessage;
import com.windev.flight_service.repository.FlightRepository;
import com.windev.flight_service.repository.SeatRepository;
import com.windev.flight_service.service.FlightService;
import com.windev.flight_service.service.cache.FlightCacheService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightEventListener {
    private final ObjectMapper objectMapper;

    private final SeatRepository seatRepository;

    private final FlightRepository flightRepository;

    private final FlightCacheService flightCacheService;

    private final FlightMapper flightMapper;

    private final FlightService flightService;

    @KafkaListener(topics = "booking-topic", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleEvent(@Payload String message){
        try {
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);
            BookingEvent data = objectMapper.convertValue(eventMessage.getData(), BookingEvent.class);
            String flightId = data.getFlightId();

            Flight flight = flightRepository.findById(flightId)
                    .orElseThrow(() -> new FlightNotFoundException("Flight with ID: " + flightId + " not found."));

            Map<String, Seat> seatMap = flight.getSeats().stream()
                    .collect(Collectors.toMap(Seat::getSeatNumber, seat -> seat));

            data.getTickets().forEach(ticket -> {
                String seatNumber = ticket.getSeatNumber();
                Seat seat = seatMap.get(seatNumber);
                if(seat != null && seat.getIsAvailable()){
                    seat.setIsAvailable(false);
                    log.info("Seat number {} status updated " +
                                    "'available' field to 'false'.",
                            seatNumber);
                } else {
                    log.warn("Seat number {} not found or seat not available.", seatNumber);
                }
            });

            seatRepository.saveAll(flight.getSeats());

            FlightDetailDTO flightDetailDTO = flightMapper.toDetailDTO(flight);

            flightCacheService.update(flightDetailDTO);

            log.info("handleEvents() --> received data: {}", data.toString());
        } catch(Exception e){
            log.error("handleEvent() --> error handle event: {}", e.getMessage());
        }
    }


}
