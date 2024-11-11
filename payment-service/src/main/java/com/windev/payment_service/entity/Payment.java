package com.windev.payment_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    private String id;

    @Column(nullable = false)
    private String bookingId;

    private BigDecimal amount;

    private String paymentMethod;

    private String status;

    private LocalDateTime transactionDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    public void onCreate(){
        createdAt = new Date();
        updatedAt = new Date();
        transactionDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        updatedAt = new Date();
    }
}
