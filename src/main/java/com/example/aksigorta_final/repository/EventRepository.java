package com.example.aksigorta_final.repository;

import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.util.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByStatusIn(Collection<EventStatus> statuses, Pageable pageable);

    Page<Event> findByOwnerEquals(User owner, Pageable pageable);

    Page<Event> findByParticipantsIn(Collection<User> participants, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status IN :statuses AND (LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Event> searchVisibleEvents(@Param("query") String query, @Param("statuses") List<EventStatus> statuses, Pageable pageable);


}