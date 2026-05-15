package com.example.aksigorta_final.repository;

import com.example.aksigorta_final.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}