package com.windev.booking_service.controller;


import com.windev.booking_service.dto.FlightDTO;
import com.windev.booking_service.dto.UserDTO;
import com.windev.booking_service.model.Booking;
import com.windev.booking_service.payload.BookingWithPaymentResponse;
import com.windev.booking_service.payload.CreateBookingRequest;
import com.windev.booking_service.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingRequest request,
                                                 @RequestHeader("Authorization") String authHeader) {

        FlightDTO flight = bookingService.getFlightByFlightId(request.getFlightId());
        UserDTO user = bookingService.getCurrentUser(authHeader);

        Booking createdBooking = bookingService.createBooking(request, user, flight);
        return ResponseEntity.ok(createdBooking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingWithPaymentResponse> getBookingById(@PathVariable String bookingId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingWithPaymentResponse>> getAllBookings() {
        return ResponseEntity.ok().body(bookingService.getAllBookings());
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(@PathVariable String bookingId, @RequestBody Booking bookingDetails) {
        try {
            Booking updatedBooking = bookingService.updateBooking(bookingId, bookingDetails);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable String bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}