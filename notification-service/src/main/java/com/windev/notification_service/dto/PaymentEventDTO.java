/**
 * @project flight-reservation-management-system
 * @author DEV on 18/11/2024
 */

package com.windev.notification_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
public class PaymentEventDTO {
    private String id;

    private String bookingId;

    // add user id to get user information
    private String userId;

    private BigDecimal amount;

    private String paymentMethod;

    private String status;

    private LocalDateTime transactionDate;

    private Date createdAt;

    private Date updatedAt;
}
