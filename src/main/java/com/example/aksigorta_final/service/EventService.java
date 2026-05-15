package com.example.aksigorta_final.service;

import com.example.aksigorta_final.dto.EventCreateRequestDto;
import com.example.aksigorta_final.dto.EventResponseDto;
import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ModelMapper model;

    public ResponseEntity createEvent(EventCreateRequestDto eventCreateRequestDto, User sessionUser){
        Event newEvent = model.map(eventCreateRequestDto, Event.class);
        newEvent.setOwner(sessionUser);
        eventRepository.save(newEvent);
        EventResponseDto eventResponseDto = model.map(newEvent, EventResponseDto.class);
        eventResponseDto.setOwnerFullName(sessionUser.getName() + " " + sessionUser.getSurname());
        eventResponseDto.setParticipantCount(0);
        return ResponseEntity.ok().body(eventResponseDto);
    }
}
