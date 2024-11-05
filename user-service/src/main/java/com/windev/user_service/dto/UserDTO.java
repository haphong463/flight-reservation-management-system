package com.windev.user_service.dto;

import com.windev.user_service.model.Preferences;
import com.windev.user_service.model.Role;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private List<Role> roles;
    private Preferences preferences;
    private Date createdAt;
    private Date updatedAt;
}
