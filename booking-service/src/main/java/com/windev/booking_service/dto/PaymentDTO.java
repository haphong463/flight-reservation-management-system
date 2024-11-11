package com.windev.booking_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
public class PaymentDTO {
    private String id;

    private String bookingId;

    private BigDecimal amount;

    private String paymentMethod;

    private String status;

    private LocalDateTime transactionDate;

    private Date createdAt;

    private Date updatedAt;
}
