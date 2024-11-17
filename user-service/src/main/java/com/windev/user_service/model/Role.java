package com.windev.user_service.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Role {

    @Id
    private String id;

    private String name;
}
