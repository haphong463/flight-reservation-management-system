package com.windev.user_service.mapper;

import com.windev.user_service.dto.UserProfileDTO;
import com.windev.user_service.model.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileDTO toDTO(UserProfile userProfile);
}
