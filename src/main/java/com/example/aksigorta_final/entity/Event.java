package com.example.aksigorta_final.entity;

import com.example.aksigorta_final.util.EventCategory;
import com.example.aksigorta_final.util.EventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String name;
    @Column(length = 1000)
    private String description;
    @Column(name = "event_date")
    private LocalDate date;
    @Column(length = 10, name = "event_time")
    private String time;
    @Column(length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    private EventCategory category = EventCategory.GENERAL;

    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.PLANNED;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();
}
