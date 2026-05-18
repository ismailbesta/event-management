package com.example.aksigorta_final.scheduler;

import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.repository.EventRepository;
import com.example.aksigorta_final.util.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventStatusScheduler {

    private final EventRepository eventRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void refreshEventStatuses() {
        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<Event> toStart = eventRepository.findEventsToStart(EventStatus.PUBLISHED, today, nowTime);
        if (!toStart.isEmpty()) {
            toStart.forEach(event -> event.setStatus(EventStatus.ONGOING));
            eventRepository.saveAll(toStart);
        }

        List<EventStatus> activeStatuses = List.of(EventStatus.ONGOING, EventStatus.PUBLISHED);
        List<Event> toComplete = eventRepository.findEventsToComplete(activeStatuses, today, nowTime);
        if (!toComplete.isEmpty()) {
            toComplete.forEach(event -> event.setStatus(EventStatus.COMPLETED));
            eventRepository.saveAll(toComplete);
        }
    }
}