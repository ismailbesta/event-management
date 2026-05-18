package com.example.aksigorta_final.entity;

import com.example.aksigorta_final.util.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String surname;

    @Column(unique = true, length = 200, nullable = false)
    private String email;

    @Column(length = 1000, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToMany(mappedBy = "participants")
    private List<Event> joinedEvents = new ArrayList<>();
}