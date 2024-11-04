package com.windev.user_service.config;

import com.windev.user_service.model.Role;
import com.windev.user_service.repository.RoleRepository;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args){
        List<String> roleNames = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        for(String roleName : roleNames){
            if(!roleRepository.findByName(roleName).isPresent()){
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                LOGGER.info("--> Added role: {}", roleName);
            }
        }
    }
}
