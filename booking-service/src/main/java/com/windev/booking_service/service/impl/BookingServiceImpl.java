package com.windev.booking_service.service.impl;

import com.windev.booking_service.dto.*;
import com.windev.booking_service.exception.SeatNotFoundException;
import com.windev.booking_service.exception.SeatUnavailableException;
import com.windev.booking_service.feign.FlightClient;
import com.windev.booking_service.feign.PaymentClient;
import com.windev.booking_service.feign.UserClient;
import com.windev.booking_service.mapper.BookingMapper;
import com.windev.booking_service.model.Booking;
import com.windev.booking_service.model.Ticket;
import com.windev.booking_service.payload.BookingWithPaymentResponse;
import com.windev.booking_service.payload.CreateBookingRequest;
import com.windev.booking_service.repository.BookingRepository;
import com.windev.booking_service.service.BookingService;
import com.windev.booking_service.service.kafka.BookingMessageQueue;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final UserClient userClient;

    private final FlightClient flightClient;

    private final BookingMapper bookingMapper;

    private final BookingMessageQueue queue;

    private final PaymentClient paymentClient;

    public Booking createBooking(CreateBookingRequest request, String authHeader) {
        Booking booking = bookingMapper.createFromRequest(request);
        UserDTO user = getCurrentUser(authHeader);
        FlightDTO flight = getFlightById(request.getFlightId());
        if (user != null && flight != null) {
            List<String> requestedSeatNumbers =
                    request.getTickets().stream().map(Ticket::getSeatNumber).toList();

            List<SeatDTO> availableSeats = flight.getSeats();

            List<SeatDTO> seatsToBook = requestedSeatNumbers.stream()
                    .map(seatNumber -> availableSeats.stream()
                            .filter(seat -> seat.getSeatNumber().equals(seatNumber))
                            .findFirst()
                            .orElseThrow(() -> new SeatNotFoundException("Ghế " + seatNumber + " không tồn tại.")))
                    .collect(Collectors.toList());

            for (SeatDTO seat : seatsToBook) {
                if (!seat.getIsAvailable()) {
                    throw new SeatUnavailableException("Ghế " + seat.getSeatNumber() + " không khả dụng.");
                }
            }

            List<Ticket> tickets = seatsToBook.stream().map(s -> {
                Ticket ticket = new Ticket();
                ticket.setTicketClass(s.getType());
                ticket.setSeatNumber(s.getSeatNumber());
                ticket.setPrice(s.getPrice());
                return ticket;
            }).toList();

            booking.setUserId(user.getId());
            booking.setPaymentId(System.currentTimeMillis() + UUID.randomUUID().toString());
            booking.setStatus("OK");
            booking.setTickets(tickets);
            log.info("Save booking reservation ok");

            Booking result = bookingRepository.save(booking);


            BookingDTO resultDTO = bookingMapper.toDTO(result);
            resultDTO.setPaymentMethod(request.getPaymentMethod());

            queue.sendMessage(resultDTO, "test-booking");

            return result;
        }
        log.warn("Error fetch user/ flight...");
        return null;
    }

    public BookingWithPaymentResponse getBookingById(String bookingId) {

        ResponseEntity<PaymentDTO> response = paymentClient.getPaymentByBookingId(bookingId);

        PaymentDTO payment = response.getBody();

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not " +
                "found with ID: " + bookingId));

        BookingWithPaymentResponse bookingWithPaymentResponse = new BookingWithPaymentResponse(booking, payment);

        log.info("getBookingById() --> bookingWithPaymentResponse: {}", bookingWithPaymentResponse);

        return bookingWithPaymentResponse;
    }

    public List<BookingWithPaymentResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        ResponseEntity<List<PaymentDTO>> paymentResponse = paymentClient.getAllPayment();
        List<PaymentDTO> payments = paymentResponse.getBody();

        Map<String, PaymentDTO> paymentMap = payments.stream()
                .collect(Collectors.toMap(PaymentDTO::getBookingId, Function.identity()));

        List<BookingWithPaymentResponse> responses = bookings.stream()
                .map(booking -> new BookingWithPaymentResponse(
                        booking,
                        paymentMap.getOrDefault(booking.getId(), null)
                ))
                .collect(Collectors.toList());

        return responses;
    }

    public Booking updateBooking(String bookingId, Booking bookingDetails) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    booking.setStatus(bookingDetails.getStatus());
                    booking.setUpdatedAt(new Date());
                    return bookingRepository.save(booking);
                }).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public void deleteBooking(String bookingId) {
        bookingRepository.findById(bookingId)
                .ifPresent(bookingRepository::delete);
    }

    private UserDTO getCurrentUser(String authHeader) {
        try {
            ResponseEntity<UserDTO> response = userClient.getCurrentUser(authHeader);
            HttpStatusCode httpStatusCode = response.getStatusCode();
            if (!httpStatusCode.is5xxServerError()) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private FlightDTO getFlightById(String id) {
        try {
            ResponseEntity<FlightDTO> response = flightClient.getFlightById(id);
            HttpStatusCode httpStatusCode = response.getStatusCode();
            if (!httpStatusCode.is5xxServerError() || !httpStatusCode.is4xxClientError()) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
