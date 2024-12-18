package com.windev.booking_service.repository;

import com.windev.booking_service.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String> {
    @Override
    Page<Booking> findAll(Pageable pageable);
}
