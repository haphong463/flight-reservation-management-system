package com.windev.booking_service.service;

import com.windev.booking_service.model.Booking;
import com.windev.booking_service.payload.CreateBookingRequest;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking createBooking(CreateBookingRequest request, String authHeader);

    Optional<Booking> getBookingById(String bookingId);

    List<Booking> getAllBookings();

    Booking updateBooking(String bookingId, Booking bookingDetails);

    void deleteBooking(String bookingId);
}
