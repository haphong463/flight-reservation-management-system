/**
 * @project flight-reservation-management-system
 * @author DEV on 21/11/2024
 */

package com.windev.notification_service.dto;

import lombok.Data;

@Data
public class UserEvent {
    private String email;
    private String token;
}
