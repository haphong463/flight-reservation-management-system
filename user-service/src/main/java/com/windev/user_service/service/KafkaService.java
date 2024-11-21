package com.windev.user_service.service;

import com.windev.user_service.event.PasswordForgotEvent;
import com.windev.user_service.event.UserRegisteredEvent;
import com.windev.user_service.model.User;

public interface KafkaService {
    void sendUserRegisteredMessage(UserRegisteredEvent event, String eventType);
    void sendPasswordForgotMessage(PasswordForgotEvent event, String eventType);
}
