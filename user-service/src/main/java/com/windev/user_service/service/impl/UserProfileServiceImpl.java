package com.windev.user_service.service.impl;

import com.windev.user_service.dto.UserProfileDTO;
import com.windev.user_service.mapper.UserProfileMapper;
import com.windev.user_service.model.Address;
import com.windev.user_service.model.User;
import com.windev.user_service.model.UserProfile;
import com.windev.user_service.payload.request.user_profile.AddressRequest;
import com.windev.user_service.payload.request.user_profile.UserProfileRequest;
import com.windev.user_service.repository.UserProfileRepository;
import com.windev.user_service.repository.UserRepository;
import com.windev.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public UserProfileDTO createUserProfile(String userId, UserProfileRequest request) {
        User user =
                userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with ID: " + userId + " " +
                        "not found."));


        UserProfile userProfile = new UserProfile();

        userProfile.setUserId(user.getId());
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setPhone(request.getPhone());
        userProfile.setDob(request.getDob());

        if (request.getAddress() != null) {
            Address address = new Address();
            AddressRequest addressRequest = request.getAddress();

            address.setCity(addressRequest.getCity());
            address.setState(addressRequest.getState());
            address.setCountry(addressRequest.getCountry());
            address.setZipCode(addressRequest.getZipCode());
            address.setStreet(addressRequest.getStreet());

            userProfile.setAddress(address);
        }

        log.info("createUserProfile() --> user profile successfully created");
        UserProfile result = userProfileRepository.save(userProfile);
        return userProfileMapper.toDTO(result);
    }

    @Override
    public UserProfileDTO getUserProfile(String userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User Profile not found for user ID: " + userId));

        return userProfileMapper.toDTO(userProfile);
    }


    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(String userId, UserProfileRequest request) {
        User user =
                userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with ID: " + userId + " " +
                        "not found."));

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User Profile not found for user ID: " + userId));

        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setPhone(request.getPhone());
        userProfile.setDob(request.getDob());

        if (request.getAddress() != null) {
            Address address = userProfile.getAddress();
            AddressRequest addressRequest = request.getAddress();

            address.setCity(addressRequest.getCity());
            address.setState(addressRequest.getState());
            address.setCountry(addressRequest.getCountry());
            address.setZipCode(addressRequest.getZipCode());
            address.setStreet(addressRequest.getStreet());

        }
        log.info("createUserProfile() --> user profile successfully created");
        UserProfile result = userProfileRepository.save(userProfile);
        return userProfileMapper.toDTO(result);
    }
}
