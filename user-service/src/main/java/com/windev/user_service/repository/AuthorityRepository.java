package com.windev.user_service.repository;

import com.windev.user_service.model.Authority;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorityRepository extends MongoRepository<Authority, String> {
    Optional<Authority> findByName(String name);
}
