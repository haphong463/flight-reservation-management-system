package com.windev.user_service.repository;

import com.windev.user_service.model.ForgotPasswordToken;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForgotPasswordTokenRepository extends MongoRepository<ForgotPasswordToken, String> {
    Optional<ForgotPasswordToken> findByToken(String token);
}
