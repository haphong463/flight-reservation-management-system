package com.windev.flight_service.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "flights")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Flight {

    @Id
    private String id;

    @Length(max = 10)
    private String flightNumber;

    @Length(max = 100)
    private String airline;

    @Length(max = 100)
    private String origin;

    @Length(max = 100)
    private String destination;

    @Temporal(TemporalType.TIMESTAMP)
    private Date departureTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivalTime;

    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();


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
