package com.windev.notification_service.dto;

import lombok.Data;

@Data
public class EventMessage {
    private String eventType;
    private Object data;
}
