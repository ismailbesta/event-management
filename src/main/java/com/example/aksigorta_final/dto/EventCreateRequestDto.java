package com.example.aksigorta_final.dto;

import com.example.aksigorta_final.util.EventCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link com.example.aksigorta_final.entity.Event}
 */
@Data
public class EventCreateRequestDto{
    @Size(min = 1, max = 200)
    @NotBlank
    String name;
    @Size(min = 1, max = 1000)
    @NotBlank
    String description;
    @NotNull
    @FutureOrPresent
    LocalDate startDate;
    @NotNull
    @Future
    LocalDate endDate;
    @NotNull
    LocalTime startTime;
    @NotNull
    LocalTime endTime;
    @Size(min = 1, max = 100)
    @NotBlank
    String location;
    @NotNull
    EventCategory category;
}