package com.windev.user_service.repository;

import com.windev.user_service.model.EmailVerificationToken;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailVerificationTokenRepository extends MongoRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByToken(String token);
}
