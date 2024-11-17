package com.windev.user_service.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@Builder
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    private String email;

    @DBRef
    private List<Role> roles;

    @DBRef
    private List<Authority> authorities;

    private Preferences preferences;

    private boolean emailVerified;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
