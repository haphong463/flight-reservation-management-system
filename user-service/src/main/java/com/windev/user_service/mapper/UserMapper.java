package com.windev.user_service.mapper;

import com.windev.user_service.dto.UserDTO;
import com.windev.user_service.dto.UserProfileDTO;
import com.windev.user_service.model.User;
import com.windev.user_service.model.UserProfile;
import com.windev.user_service.payload.response.UserRegisteredResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserRegisteredResponse toUserRegisteredResponse(User user);

    @Mapping(target = "authorities", source = "authorities")
    UserDTO toUserDTO(User user);
}
