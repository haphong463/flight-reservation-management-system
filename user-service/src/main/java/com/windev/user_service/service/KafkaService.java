package com.windev.user_service.service;

import com.windev.user_service.model.User;

public interface KafkaService {
    void sendMessage(User user, String eventType);
}
