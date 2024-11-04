package com.windev.user_service.repository;

import com.windev.user_service.model.Role;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}
