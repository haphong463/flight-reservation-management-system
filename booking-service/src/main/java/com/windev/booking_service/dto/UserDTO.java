package com.windev.booking_service.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private UserProfileDTO profile;
}
