package com.windev.user_service.service;

import com.windev.user_service.dto.UserProfileDTO;
import com.windev.user_service.model.UserProfile;
import com.windev.user_service.payload.request.user_profile.UserProfileRequest;

public interface UserProfileService {
    UserProfileDTO updateUserProfile(String userId, UserProfileRequest request);

    UserProfileDTO createUserProfile(String userId, UserProfileRequest request);

    UserProfileDTO getUserProfile(String userId);
}
