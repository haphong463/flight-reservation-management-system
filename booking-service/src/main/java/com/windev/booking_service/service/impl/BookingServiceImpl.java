package com.windev.booking_service.service.impl;

import com.windev.booking_service.dto.*;
import com.windev.booking_service.exception.*;
import com.windev.booking_service.feign.FlightClient;
import com.windev.booking_service.feign.PaymentClient;
import com.windev.booking_service.feign.UserClient;
import com.windev.booking_service.mapper.BookingMapper;
import com.windev.booking_service.model.Booking;
import com.windev.booking_service.model.Ticket;
import com.windev.booking_service.payload.BookingWithPaymentResponse;
import com.windev.booking_service.payload.CreateBookingRequest;
import com.windev.booking_service.payload.PaginatedResponse;
import com.windev.booking_service.repository.BookingRepository;
import com.windev.booking_service.service.BookingService;
import com.windev.booking_service.service.kafka.BookingMessageQueue;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final static String USER_SERVICE = "USER-SERVICE";
    private final static String FLIGHT_SERVICE = "FLIGHT-SERVICE";
    private final static String PAYMENT_SERVICE = "PAYMENT-SERVICE";

    @Override
    public Booking createBooking(CreateBookingRequest request, UserDTO user, FlightDTO flight) {
        Booking booking = bookingMapper.createFromRequest(request);

        List<String> requestedSeatNumbers =
                request.getTickets().stream().map(Ticket::getSeatNumber).toList();

        List<SeatDTO> availableSeats = flight.getSeats();

        List<SeatDTO> seatsToBook = requestedSeatNumbers.stream()
                .map(seatNumber -> availableSeats.stream()
                        .filter(seat -> seat.getSeatNumber().equals(seatNumber))
                        .findFirst()
                        .orElseThrow(() -> new SeatNotFoundException("Seat number " + seatNumber + " not found.")))
                .collect(Collectors.toList());

        for (SeatDTO seat : seatsToBook) {
            if (!seat.getIsAvailable()) {
                throw new SeatUnavailableException("Seat number " + seat.getSeatNumber() + " is unavailable.");
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

    @Override
    public BookingWithPaymentResponse getBookingById(String bookingId) {

        ResponseEntity<PaymentDTO> response = paymentClient.getPaymentByBookingId(bookingId);

        PaymentDTO payment = response.getBody();

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not " +
                "found with ID: " + bookingId));

        ResponseEntity<List<UserDTO>> response2 = userClient.getAllUsers(Set.of(booking.getUserId()));

        UserDTO user = response2.getBody().get(0);

        BookingWithPaymentResponse bookingWithPaymentResponse = new BookingWithPaymentResponse(booking, payment, user);

        log.info("getBookingById() --> bookingWithPaymentResponse: {}", bookingWithPaymentResponse);

        return bookingWithPaymentResponse;
    }

    @Override
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackGetAllUsers")
    public PaginatedResponse<BookingWithPaymentResponse> getAllBookings(List<PaymentDTO> payments,
                                                                        int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Booking> bookingPage = bookingRepository.findAll(pageable);

        Set<String> userIds = bookingPage.getContent().stream().map(Booking::getUserId).collect(Collectors.toSet());

        Map<String, PaymentDTO> paymentMap = payments.stream()
                .collect(Collectors.toMap(PaymentDTO::getBookingId, Function.identity()));

        Map<String, UserDTO> userMap = userClient.getAllUsers(userIds).getBody().stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        List<BookingWithPaymentResponse> responses = bookingPage.stream()
                .map(booking -> new BookingWithPaymentResponse(
                        booking,
                        paymentMap.getOrDefault(booking.getId(), null),
                        userMap.getOrDefault(booking.getUserId(), null)
                ))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(responses,
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.isLast(),
                bookingPage.getTotalPages(),
                bookingPage.getTotalElements());
    }


    @Override
    public Booking updateBooking(String bookingId, Booking bookingDetails) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    booking.setStatus(bookingDetails.getStatus());
                    booking.setUpdatedAt(new Date());
                    return bookingRepository.save(booking);
                }).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Override
    public void deleteBooking(String bookingId) {
        bookingRepository.findById(bookingId)
                .ifPresent(bookingRepository::delete);
    }

    @Override
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackGetCurrentUser")
    public UserDTO getCurrentUser(String authHeader) {
        ResponseEntity<UserDTO> response = userClient.getCurrentUser(authHeader);
        return response.getBody();
    }

    @Override
    @CircuitBreaker(name = FLIGHT_SERVICE, fallbackMethod = "fallbackGetFlightByFlightId")
    public FlightDTO getFlightByFlightId(String flightId) {
        ResponseEntity<FlightDTO> response = flightClient.getFlightById(flightId);
        return response.getBody();
    }

    @Override
    @CircuitBreaker(name = PAYMENT_SERVICE, fallbackMethod = "fallbackGetAllPayments")
    public List<PaymentDTO> getAllPayments() {
        ResponseEntity<List<PaymentDTO>> paymentResponse = paymentClient.getAllPayment();
        return paymentResponse.getBody();
    }


    public PaginatedResponse<UserDTO> fallbackGetAllUsers(Throwable throwable) {
        log.error("fallbackGetAllUsers() --> {}", throwable.getMessage());
        throw new GetPaymentException("Can't get all users.");
    }

    public List<PaymentDTO> fallbackGetAllPayments(Throwable throwable) {
        log.error("fallbackGetAllPayments() --> {}", throwable.getMessage());
        throw new GetPaymentException("Can't get all payments.");
    }

    public UserDTO fallbackGetCurrentUser(Throwable throwable) {
        log.error("fallbackGetCurrentUser() --> {}", throwable.getMessage());
        throw new AuthenticatedException("Can't get current user information.");
    }

    public FlightDTO fallbackGetFlightByFlightId(Throwable throwable) {
        log.error("fallbackGetFlightByFlightId() --> {}", throwable.getMessage());
        throw new FlightNotFoundException("Can't get flight information.");
    }
}
