package com.example.aksigorta_final.repository;

import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.util.EventCategory;
import com.example.aksigorta_final.util.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByStatusIn(Collection<EventStatus> statuses, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status IN :statuses AND (LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Event> searchVisibleEvents(@Param("query") String query, @Param("statuses") List<EventStatus> statuses, Pageable pageable);

    Page<Event> findByCategoryAndStatusIn(EventCategory category, Collection<EventStatus> statuses, Pageable pageable);

    Page<Event> findByParticipantsAndStatusNot(User participant, EventStatus status, Pageable pageable);

    Page<Event> findByOwnerAndStatus(User owner, EventStatus status, Pageable pageable);

    Page<Event> findByOwnerAndStatusNot(User owner, EventStatus status, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND (e.startDate < :today OR (e.startDate = :today AND e.startTime <= :nowTime))")
    List<Event> findEventsToStart(@Param("status") EventStatus status, @Param("today") LocalDate today, @Param("nowTime") LocalTime nowTime);

    @Query("SELECT e FROM Event e WHERE e.status IN :statuses AND (e.endDate < :today OR (e.endDate = :today AND e.endTime <= :nowTime))")
    List<Event> findEventsToComplete(@Param("statuses") List<EventStatus> statuses, @Param("today") LocalDate today, @Param("nowTime") LocalTime nowTime);
}