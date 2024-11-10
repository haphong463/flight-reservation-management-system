package com.windev.booking_service.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventMessage {
    private Object data;
    private String eventType;
}