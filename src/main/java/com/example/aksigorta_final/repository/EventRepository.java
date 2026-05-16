package com.example.aksigorta_final.repository;

import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.util.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByStatusIn(Collection<EventStatus> statuses, Pageable pageable);

    List<Event> findByOwner_EmailIgnoreCase(String email);

    Page<Event> findByOwnerEquals(User owner, Pageable pageable);
}