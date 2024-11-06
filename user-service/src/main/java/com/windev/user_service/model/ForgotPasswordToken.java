package com.windev.user_service.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "password_reset_tokens")
public class ForgotPasswordToken {
    @Id
    private String id;
    private String token;
    private String userId;
    private Date createdAt;

    @Indexed(expireAfterSeconds = 0)
    private Date expiresAt;

    private boolean isUsed;

    public ForgotPasswordToken(){
        this.createdAt = new Date();
        this.expiresAt = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        this.isUsed = false;
    }
}
