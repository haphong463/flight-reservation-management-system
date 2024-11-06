package com.windev.flight_service.repository;

import com.windev.flight_service.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, String> {
    @Override
    Page<Flight> findAll(Pageable pageable);
}
