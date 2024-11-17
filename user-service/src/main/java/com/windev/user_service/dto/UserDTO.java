package com.windev.user_service.dto;

import com.windev.user_service.model.Authority;
import com.windev.user_service.model.Preferences;
import com.windev.user_service.model.Role;
import com.windev.user_service.model.UserProfile;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private List<Role> roles;
    private List<Authority> authorities;
    private Preferences preferences;
    private UserProfileDTO profile;
    private Date createdAt;
    private Date updatedAt;
}
