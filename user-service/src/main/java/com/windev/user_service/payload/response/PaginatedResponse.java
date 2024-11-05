package com.windev.user_service.payload.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> data;
    private int pageNumber;
    private int pageSize;
    private boolean isLast;
    private int totalPages;
    private long totalElements;
}
