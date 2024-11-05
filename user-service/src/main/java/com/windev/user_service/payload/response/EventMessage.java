package com.windev.user_service.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventMessage {
    private Object data;
    private String eventType;
}
