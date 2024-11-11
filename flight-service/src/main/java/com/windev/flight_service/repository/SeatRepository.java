package com.windev.flight_service.repository;

import com.windev.flight_service.entity.Flight;
import com.windev.flight_service.entity.Seat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, String> {
    Optional<Seat> findByFlightAndSeatNumber(Flight flight, String seatNumber);
    List<Seat> findByFlight(Flight flight);
}
