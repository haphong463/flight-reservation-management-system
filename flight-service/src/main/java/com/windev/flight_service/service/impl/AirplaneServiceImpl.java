package com.windev.flight_service.service.impl;

import com.windev.flight_service.dto.AirplaneDTO;
import com.windev.flight_service.entity.Airplane;
import com.windev.flight_service.exception.AirplaneNotFoundException;
import com.windev.flight_service.mapper.AirplaneMapper;
import com.windev.flight_service.payload.request.airplane.AirplaneRequest;
import com.windev.flight_service.payload.response.PaginatedResponse;
import com.windev.flight_service.repository.AirplaneRepository;
import com.windev.flight_service.service.AirplaneService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AirplaneServiceImpl implements AirplaneService {
    private final AirplaneRepository airplaneRepository;
    private final AirplaneMapper airplaneMapper;

    @Override
    public AirplaneDTO createAirplane(AirplaneRequest request) {
        Airplane airplane = airplaneMapper.toEntity(request);

        airplaneRepository.save(airplane);
        log.info("createAirplane() --> airplane successfully created");
        return airplaneMapper.toDTO(airplane);
    }

    @Override
    public void deleteAirplane(String id) {
        Airplane existingAirplane = airplaneRepository.findById(id)
                .orElseThrow(() -> new AirplaneNotFoundException("Airplane with ID: " + id + " not found."));

        airplaneRepository.delete(existingAirplane);
        log.info("deleteAirplane() --> airplane successfully deleted");
    }

    @Override
    public PaginatedResponse<AirplaneDTO> getAllAirplanes(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Airplane> airplanePage = airplaneRepository.findAll(pageable);

        List<AirplaneDTO> list = airplanePage.map(airplaneMapper::toDTO).toList();

        return new PaginatedResponse<>(list, airplanePage.getNumber(), airplanePage.getSize(),
                airplanePage.getTotalElements(), airplanePage.getTotalPages(), airplanePage.isLast());
    }
}
