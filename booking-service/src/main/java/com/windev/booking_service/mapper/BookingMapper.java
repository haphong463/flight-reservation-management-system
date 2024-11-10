package com.windev.booking_service.mapper;

import com.windev.booking_service.dto.BookingDTO;
import com.windev.booking_service.model.Booking;
import com.windev.booking_service.payload.CreateBookingRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDTO toDTO(Booking booking);

    Booking createFromRequest(CreateBookingRequest request);
}
