package com.example.aksigorta_final.controller;

import com.example.aksigorta_final.dto.EventCreateRequestDto;
import com.example.aksigorta_final.dto.EventResponseDto;
import com.example.aksigorta_final.dto.UserResponseDto;
import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("event")
public class EventRestController {
    private final EventService eventService;

    @PostMapping("create")
    public ResponseEntity createEvent(@Valid @RequestBody EventCreateRequestDto eventCreateRequestDto, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.createEvent(eventCreateRequestDto, sessionUser);
    }

    @PostMapping("join/{eventId}")
    public ResponseEntity joinEvent(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.joinEvent(eventId, sessionUser);
    }

    @PostMapping("publish/{eventId}")
    public ResponseEntity publishEvent(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.publishEvent(eventId, sessionUser);
    }

    @GetMapping("list")
    public Page<EventResponseDto> listEvents(@RequestParam(defaultValue = "0") int page){
        return eventService.eventList(page);
    }

    @GetMapping("get/{eventId}")
    public ResponseEntity getEventById(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.getEventById(eventId, sessionUser);
    }

    @GetMapping("my-events")
    public Page<EventResponseDto> ListMyEvents(@RequestParam(defaultValue = "0") int page, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.listMyEvents(page, sessionUser);
    }

    @PostMapping("cancel")
    public ResponseEntity cancelEvent(@RequestParam Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.cancelEvent(eventId, sessionUser);
    }

}
