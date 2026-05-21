package com.example.aksigorta_final.controller;

import com.example.aksigorta_final.dto.EventCreateRequestDto;
import com.example.aksigorta_final.dto.EventResponseDto;
import com.example.aksigorta_final.dto.EventUpdateRequestDto;
import com.example.aksigorta_final.dto.UserResponseDto;
import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.service.EventService;
import com.example.aksigorta_final.util.EventCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Event", description = "Event management operations")
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

    @PatchMapping("publish/{eventId}")
    public ResponseEntity publishEvent(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.publishEvent(eventId, sessionUser);
    }

    @GetMapping("list")
    public Page<EventResponseDto> listEvents(@RequestParam(defaultValue = "0") int page, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.eventList(page, sessionUser);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity getEventById(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.getEventById(eventId, sessionUser);
    }

    @GetMapping("my-events")
    public Page<EventResponseDto> ListMyEvents(@RequestParam(defaultValue = "0") int page, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.listMyEvents(page, sessionUser);
    }

    @PatchMapping("cancel/{eventId}")
    public ResponseEntity cancelEvent(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.cancelEvent(eventId, sessionUser);
    }

    @PostMapping("leave/{eventId}")
    public ResponseEntity leaveEvent(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.leaveEvent(eventId, sessionUser);
    }

    @GetMapping("joined-events")
    public Page<EventResponseDto> listJoinedEvents(@RequestParam(defaultValue = "0") int page, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.listJoinedEvents(page, sessionUser);
    }

    @GetMapping("search")
    public Page<EventResponseDto> search(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "asc")String startDate
    ){
        return eventService.search(q, page, startDate);
    }

    @PatchMapping("unpublish/{eventId}")
    public ResponseEntity unpublishEvent(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.unpublishEvent(eventId, sessionUser);
    }

    @GetMapping("participants/{eventId}")
    public ResponseEntity getEventParticipants(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request
    ){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.getEventParticipants(page, eventId, sessionUser);
    }

    @PutMapping("update")
    public ResponseEntity updateEvent(@Valid @RequestBody EventUpdateRequestDto eventUpdateRequestDto, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.updateEvent(eventUpdateRequestDto, sessionUser);
    }

    @GetMapping("category/{categoryName}")
    public ResponseEntity getEventsByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        EventCategory category = EventCategory.valueOf(categoryName.toUpperCase());
        Page<EventResponseDto> response = eventService.listEventByCategory(category, page, sessionUser);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("archive/{eventId}")
    public ResponseEntity archiveEvent(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.archiveEvent(eventId, sessionUser);
    }

    @GetMapping("archived-events")
    public Page<EventResponseDto> listArchivedEvents(@RequestParam(defaultValue = "0") int page, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.listMyArchivedEvents(page, sessionUser);
    }

    @GetMapping("user-role/{eventId}")
    public ResponseEntity getEventUserRole(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.getUserEventRole(eventId, sessionUser);
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity deleteEvent(@PathVariable Long eventId, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.deleteOne(eventId, sessionUser);
    }

}
