package com.windev.booking_service.controller;


import com.windev.booking_service.dto.FlightDTO;
import com.windev.booking_service.dto.PaymentDTO;
import com.windev.booking_service.dto.UserDTO;
import com.windev.booking_service.model.Booking;
import com.windev.booking_service.payload.BookingWithPaymentResponse;
import com.windev.booking_service.payload.CreateBookingRequest;
import com.windev.booking_service.payload.PaginatedResponse;
import com.windev.booking_service.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Booking Management", description = "APIs for managing reservations")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Create a new reservation (authentication required)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation successfully created",
                    content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Reservation creation request",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateBookingRequest.class)))
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingRequest request,
                                                 @RequestHeader("Authorization") String authHeader) {

        FlightDTO flight = bookingService.getFlightByFlightId(request.getFlightId());
        UserDTO user = bookingService.getCurrentUser(authHeader);

        Booking createdBooking = bookingService.createBooking(request, user, flight);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a booking by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found",
                    content = @Content(schema = @Schema(implementation = BookingWithPaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                    content = @Content)
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingWithPaymentResponse> getBookingById(@PathVariable String bookingId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(bookingId));
    }

    @Operation(summary = "Get all bookings with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved",
                    content = @Content(schema = @Schema(implementation = PaginatedResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<BookingWithPaymentResponse>> getAllBookings(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<PaymentDTO> payments = bookingService.getAllPayments();

        return ResponseEntity.ok().body(bookingService.getAllBookings(payments, pageNumber, pageSize));
    }

    @Operation(summary = "Update a booking by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking successfully updated",
                    content = @Content(schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                    content = @Content)
    })
    @PutMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(@PathVariable String bookingId,
                                                 @RequestBody Booking bookingDetails) {
        try {
            Booking updatedBooking = bookingService.updateBooking(bookingId, bookingDetails);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a booking by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable String bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
