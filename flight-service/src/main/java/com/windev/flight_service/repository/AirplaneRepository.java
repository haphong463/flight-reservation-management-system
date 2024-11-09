package com.windev.flight_service.repository;

import com.windev.flight_service.entity.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirplaneRepository extends JpaRepository<Airplane, String> {
}
