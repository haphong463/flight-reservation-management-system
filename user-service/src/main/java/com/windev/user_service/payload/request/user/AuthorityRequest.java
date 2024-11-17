package com.windev.user_service.payload.request.user;

import java.util.Set;
import lombok.Data;

@Data
public class AuthorityRequest {
    private Set<String> names;
}
