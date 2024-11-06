package com.windev.flight_service.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "seats")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "flight_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Flight flight;

    private String seatNumber;

    private String type;

    private Boolean isAvailable;

    private Double price;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    public void onCreate(){
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    public void onUpdate(){
        updatedAt = new Date();
    }
}
