package com.windev.booking_service.payload;

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
