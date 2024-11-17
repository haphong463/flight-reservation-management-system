package com.windev.user_service.config;

import com.windev.user_service.model.Authority;
import com.windev.user_service.model.User;
import com.windev.user_service.repository.AuthorityRepository;

import com.windev.user_service.repository.UserRepository;
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

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    @Autowired
    public DataInitializer(AuthorityRepository authorityRepository, UserRepository userRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Danh sách các quyền (Authorities)
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

        // Tạo các quyền nếu chưa tồn tại
        for (String authorityName : authorityNames) {
            if (!authorityRepository.findByName(authorityName).isPresent()) {
                Authority authority = new Authority();
                authority.setName(authorityName);
                authorityRepository.save(authority);
                LOGGER.info("--> Added authority: {}", authorityName);
            }
        }

//        Map<String, Authority> authoritiesMap = authorityRepository.findAll().stream()
//                .collect(Collectors.toMap(Authority::getName, Function.identity()));
//
//        // Danh sách User và quyền của họ
//        Map<String, List<Authority>> usersAndAuthorities = new HashMap<>();
//
//        usersAndAuthorities.put("admin", Arrays.asList(
//                authoritiesMap.get("FLIGHT_VIEW"),
//                authoritiesMap.get("FLIGHT_CREATE"),
//                authoritiesMap.get("FLIGHT_UPDATE"),
//                authoritiesMap.get("FLIGHT_DELETE"),
//                authoritiesMap.get("RESERVATION_VIEW"),
//                authoritiesMap.get("RESERVATION_CREATE"),
//                authoritiesMap.get("RESERVATION_UPDATE"),
//                authoritiesMap.get("RESERVATION_CANCEL"),
//                authoritiesMap.get("USER_MANAGE"),
//                authoritiesMap.get("REPORT_VIEW"),
//                authoritiesMap.get("PAYMENT_PROCESS"),
//                authoritiesMap.get("CHECKIN_MANAGE"),
//                authoritiesMap.get("SEAT_ASSIGN"),
//                authoritiesMap.get("BAGGAGE_MANAGE"),
//                authoritiesMap.get("NOTIFICATION_SEND")
//        ));
//
//        usersAndAuthorities.put("agent", Arrays.asList(
//                authoritiesMap.get("FLIGHT_VIEW"),
//                authoritiesMap.get("RESERVATION_VIEW"),
//                authoritiesMap.get("RESERVATION_CREATE"),
//                authoritiesMap.get("RESERVATION_UPDATE"),
//                authoritiesMap.get("RESERVATION_CANCEL"),
//                authoritiesMap.get("PAYMENT_PROCESS"),
//                authoritiesMap.get("SEAT_ASSIGN"),
//                authoritiesMap.get("NOTIFICATION_SEND")
//        ));
//
//        usersAndAuthorities.put("customer", Arrays.asList(
//                authoritiesMap.get("FLIGHT_VIEW"),
//                authoritiesMap.get("RESERVATION_VIEW"),
//                authoritiesMap.get("RESERVATION_CREATE"),
//                authoritiesMap.get("RESERVATION_CANCEL")
//        ));
//
//        usersAndAuthorities.put("checkin_staff", Arrays.asList(
//                authoritiesMap.get("CHECKIN_MANAGE"),
//                authoritiesMap.get("FLIGHT_VIEW"),
//                authoritiesMap.get("RESERVATION_VIEW"),
//                authoritiesMap.get("SEAT_ASSIGN"),
//                authoritiesMap.get("BAGGAGE_MANAGE")
//        ));
//
//        usersAndAuthorities.put("support_staff", Arrays.asList(
//                authoritiesMap.get("RESERVATION_VIEW"),
//                authoritiesMap.get("RESERVATION_UPDATE"),
//                authoritiesMap.get("NOTIFICATION_SEND"),
//                authoritiesMap.get("BAGGAGE_MANAGE")
//        ));
//
//        // Tạo User với quyền được gán
//        for (Map.Entry<String, List<Authority>> entry : usersAndAuthorities.entrySet()) {
//            String username = entry.getKey();
//            List<Authority> authorities = entry.getValue();
//
//            Optional<User> userOpt = userRepository.findByUsername(username);
//            if (!userOpt.isPresent()) {
//                User user = new User();
//                user.setUsername(username);
//                user.setEmail(username + "@example.com");
//                user.setPassword("password"); // Thay thế bằng mã hóa trong thực tế
//                user.setAuthorities(authorities);
//                userRepository.save(user);
//                LOGGER.info("--> Added user: {}", username);
//            } else {
//                // Cập nhật authorities cho User nếu cần thiết
//                User user = userOpt.get();
//                user.setAuthorities(authorities);
//                userRepository.save(user);
//                LOGGER.info("--> Updated user: {}", username);
//            }
//        }
    }
}
