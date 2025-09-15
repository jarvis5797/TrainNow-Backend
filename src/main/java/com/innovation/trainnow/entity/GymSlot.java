package com.innovation.trainnow.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "gym_slot")
@Getter
@Setter
@RequiredArgsConstructor
public class GymSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal pricePerHour;
    private Boolean isAvailable = true;

    @ManyToMany(mappedBy = "slots")
    private List<Booking> bookings;
}

