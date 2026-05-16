package com.example.aksigorta_final.dto;

import com.example.aksigorta_final.util.EventCategory;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    LocalDate date;
    @NotNull
    LocalTime time;
    @Size(min = 1, max = 100)
    @NotBlank
    String location;
    @NotNull
    EventCategory category;
}