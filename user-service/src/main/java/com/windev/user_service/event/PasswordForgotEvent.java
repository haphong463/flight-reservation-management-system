/**
 * @project flight-reservation-management-system
 * @author DEV on 21/11/2024
 */

package com.windev.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordForgotEvent {
    private String email;
    private String token;
}
