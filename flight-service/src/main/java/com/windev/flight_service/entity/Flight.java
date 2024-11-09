package com.windev.flight_service.entity;

import com.windev.flight_service.enums.FlightStatus;
import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "flights")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flight {

    @Id
    private String id;

    @Length(max = 25)
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

    @ManyToMany
    @JoinTable(
            name = "flight_crew",
            joinColumns = @JoinColumn(name = "flight_id"),
            inverseJoinColumns = @JoinColumn(name = "crew_id")
    )
    private Set<Crew> crews = new HashSet<>();

    @ManyToOne
    @JoinColumn(
            name = "airplane_id",
            nullable = false
    )
    private Airplane airplane;


    @PrePersist
    public void onCreate() {
        id = UUID.randomUUID().toString();
        status = FlightStatus.ON_TIME.name();
        flightNumber = this.generateFlightNumber();
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = new Date();
    }

    private String generateFlightNumber() {
        return "FL-" + System.currentTimeMillis();
    }
}
