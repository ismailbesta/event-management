package com.example.aksigorta_final.dto;

import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.util.EventCategory;
import com.example.aksigorta_final.util.EventStatus;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link Event}
 */
@Data
public class EventResponseDto{
    Long id;
    String name;
    String description;
    LocalDate date;
    LocalTime time;
    String location;
    EventCategory category;
    EventStatus status;

    // etkinlik sahibinin adı + soyadı
    String ownerFullName;

    // katılımcı sayısının toplamı
    int participantCount;
}