package com.windev.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "authorities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authority {
    @Id
    private String id;

    private String name;
}
