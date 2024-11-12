package com.windev.booking_service.service;

import com.windev.booking_service.dto.FlightDTO;
import com.windev.booking_service.dto.UserDTO;
import com.windev.booking_service.model.Booking;
import com.windev.booking_service.payload.BookingWithPaymentResponse;
import com.windev.booking_service.payload.CreateBookingRequest;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking createBooking(CreateBookingRequest request, UserDTO user, FlightDTO flight);

    BookingWithPaymentResponse getBookingById(String bookingId);

    List<BookingWithPaymentResponse> getAllBookings();

    Booking updateBooking(String bookingId, Booking bookingDetails);

    void deleteBooking(String bookingId);

    UserDTO getCurrentUser(String authHeader);

    FlightDTO getFlightByFlightId(String flightId);
}
