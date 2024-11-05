package com.windev.user_service.repository;

import com.windev.user_service.model.BlacklistedToken;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistedTokenRepository extends MongoRepository<BlacklistedToken, String> {
    Optional<BlacklistedToken> findByToken(String token);
}
