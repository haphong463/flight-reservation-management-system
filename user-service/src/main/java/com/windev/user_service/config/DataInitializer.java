package com.windev.user_service.config;

import com.windev.user_service.model.Authority;
import com.windev.user_service.model.Role;
import com.windev.user_service.repository.AuthorityRepository;
import com.windev.user_service.repository.RoleRepository;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, AuthorityRepository authorityRepository){
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... args){
        List<String> authorityNames = Arrays.asList(
                "FLIGHT_VIEW",
                "FLIGHT_CREATE",
                "FLIGHT_UPDATE",
                "FLIGHT_DELETE",
                "RESERVATION_VIEW",
                "RESERVATION_CREATE",
                "RESERVATION_UPDATE",
                "RESERVATION_CANCEL",
                "USER_MANAGE",
                "REPORT_VIEW",
                "PAYMENT_PROCESS",
                "CHECKIN_MANAGE",
                "SEAT_ASSIGN",
                "BAGGAGE_MANAGE",
                "NOTIFICATION_SEND"
        );


        for (String authorityName : authorityNames) {
            if (!authorityRepository.findByName(authorityName).isPresent()) {
                Authority authority = new Authority();
                authority.setName(authorityName);
                authorityRepository.save(authority);
                LOGGER.info("--> Added authority: {}", authorityName);
            }
        }

        Map<String, Authority> authoritiesMap = authorityRepository.findAll().stream()
                .collect(Collectors.toMap(Authority::getName, Function.identity()));

        Map<String, List<Authority>> rolesAndAuthorities = new HashMap<>();

        rolesAndAuthorities.put("ROLE_ADMIN", Arrays.asList(
                authoritiesMap.get("FLIGHT_VIEW"),
                authoritiesMap.get("FLIGHT_CREATE"),
                authoritiesMap.get("FLIGHT_UPDATE"),
                authoritiesMap.get("FLIGHT_DELETE"),
                authoritiesMap.get("RESERVATION_VIEW"),
                authoritiesMap.get("RESERVATION_CREATE"),
                authoritiesMap.get("RESERVATION_UPDATE"),
                authoritiesMap.get("RESERVATION_CANCEL"),
                authoritiesMap.get("USER_MANAGE"),
                authoritiesMap.get("REPORT_VIEW"),
                authoritiesMap.get("PAYMENT_PROCESS"),
                authoritiesMap.get("CHECKIN_MANAGE"),
                authoritiesMap.get("SEAT_ASSIGN"),
                authoritiesMap.get("BAGGAGE_MANAGE"),
                authoritiesMap.get("NOTIFICATION_SEND")
        ));

        rolesAndAuthorities.put("ROLE_AGENT", Arrays.asList(
                authoritiesMap.get("FLIGHT_VIEW"),
                authoritiesMap.get("RESERVATION_VIEW"),
                authoritiesMap.get("RESERVATION_CREATE"),
                authoritiesMap.get("RESERVATION_UPDATE"),
                authoritiesMap.get("RESERVATION_CANCEL"),
                authoritiesMap.get("PAYMENT_PROCESS"),
                authoritiesMap.get("SEAT_ASSIGN"),
                authoritiesMap.get("NOTIFICATION_SEND")
        ));

        rolesAndAuthorities.put("ROLE_CUSTOMER", Arrays.asList(
                authoritiesMap.get("FLIGHT_VIEW"),
                authoritiesMap.get("RESERVATION_VIEW"),
                authoritiesMap.get("RESERVATION_CREATE"),
                authoritiesMap.get("RESERVATION_CANCEL")
        ));

        rolesAndAuthorities.put("ROLE_CHECKIN_STAFF", Arrays.asList(
                authoritiesMap.get("CHECKIN_MANAGE"),
                authoritiesMap.get("FLIGHT_VIEW"),
                authoritiesMap.get("RESERVATION_VIEW"),
                authoritiesMap.get("SEAT_ASSIGN"),
                authoritiesMap.get("BAGGAGE_MANAGE")
        ));

        rolesAndAuthorities.put("ROLE_SUPPORT_STAFF", Arrays.asList(
                authoritiesMap.get("RESERVATION_VIEW"),
                authoritiesMap.get("RESERVATION_UPDATE"),
                authoritiesMap.get("NOTIFICATION_SEND"),
                authoritiesMap.get("BAGGAGE_MANAGE")
        ));

        for (Map.Entry<String, List<Authority>> entry : rolesAndAuthorities.entrySet()) {
            String roleName = entry.getKey();
            List<Authority> authorities = entry.getValue();

            Optional<Role> roleOpt = roleRepository.findByName(roleName);
            if (!roleOpt.isPresent()) {
                Role role = new Role();
                role.setName(roleName);
                role.setAuthorities(authorities);
                roleRepository.save(role);
                LOGGER.info("--> Added role: {}", roleName);
            } else {
                // Cập nhật authorities cho role nếu cần thiết
                Role role = roleOpt.get();
                role.setAuthorities(authorities);
                roleRepository.save(role);
                LOGGER.info("--> Updated role: {}", roleName);
            }
        }
    }
}
