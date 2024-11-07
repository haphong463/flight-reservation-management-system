package com.windev.user_service.repository;

import com.windev.user_service.model.UserProfile;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByUserId(String userId);
}
