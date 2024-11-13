package com.windev.user_service.repository;

import com.windev.user_service.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAllByIdIn(Set<String> ids);
    @Override
    Page<User> findAll(Pageable pageable);
}
