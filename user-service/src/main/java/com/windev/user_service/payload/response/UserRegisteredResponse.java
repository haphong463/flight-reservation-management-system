package com.windev.user_service.payload.response;

import java.util.Date;
import lombok.Data;

@Data
public class UserRegisteredResponse {
    private String id;
    private String email;
    private String username;
    private Date createdAt;
}
