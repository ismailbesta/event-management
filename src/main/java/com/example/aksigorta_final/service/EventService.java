package com.example.aksigorta_final.service;

import com.example.aksigorta_final.dto.EventCreateRequestDto;
import com.example.aksigorta_final.dto.EventResponseDto;
import com.example.aksigorta_final.dto.ParticipantResponseDto;
import com.example.aksigorta_final.entity.Event;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.repository.EventRepository;
import com.example.aksigorta_final.util.EventStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ModelMapper model;

    public ResponseEntity createEvent(EventCreateRequestDto eventCreateRequestDto, User sessionUser){

        if (eventCreateRequestDto.getEndDate().isBefore(eventCreateRequestDto.getStartDate())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: The end date cannot be before the start date."
            ));
        }
        if (eventCreateRequestDto.getEndDate().isEqual(eventCreateRequestDto.getStartDate())) {
            if (eventCreateRequestDto.getEndTime().isBefore(eventCreateRequestDto.getStartTime())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Error: For single-day events, the end time cannot be before the start time."
                ));
            }
        }

        Event newEvent = model.map(eventCreateRequestDto, Event.class);
        newEvent.setOwner(sessionUser);
        newEvent.getParticipants().add(sessionUser);
        eventRepository.save(newEvent);
        EventResponseDto eventResponseDto = model.map(newEvent, EventResponseDto.class);
        eventResponseDto.setOwnerFullName(sessionUser.getName() + " " + sessionUser.getSurname());
        eventResponseDto.setParticipantCount(newEvent.getParticipants().size());
        return ResponseEntity.ok().body(eventResponseDto);
    }

    public ResponseEntity joinEvent(Long eventId, User sessionUser){
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: The event you are trying to join could not be found."
            );
            return ResponseEntity.badRequest().body(response);
        }

        Event event = optionalEvent.get();
        List<EventStatus> joinableStatuses = List.of(
                EventStatus.PUBLISHED,
                EventStatus.ONGOING
        );

        if (!joinableStatuses.contains(event.getStatus())) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: You can only join published or ongoing events."
            );
            return ResponseEntity.badRequest().body(response);
        }

        if (event.getParticipants().contains(sessionUser)) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: You have already joined this event."
            );
            return ResponseEntity.badRequest().body(response);
        }

        event.getParticipants().add(sessionUser); //katılımcı listesine kullanıcıyı ekle
        eventRepository.save(event); // veri tabanına kaydet
        Map<String, Object> response = Map.of(
                "success", true,
                "message", "You have successfully joined the event."
        );
        return ResponseEntity.ok().body(response);
    }

    public Page<EventResponseDto> eventList(int page) {
        Pageable pageable = Pageable.ofSize(10).withPage(page);
        List<EventStatus> visibleStatuses = List.of(
                EventStatus.PUBLISHED,
                EventStatus.ONGOING
        );
        Page<Event> eventPage = eventRepository.findByStatusIn(visibleStatuses, pageable);
        Page<EventResponseDto> response = eventPage.map(event -> {
            EventResponseDto eventResponseDto = model.map(event, EventResponseDto.class);
            eventResponseDto.setOwnerFullName(event.getOwner().getName() + " " + event.getOwner().getSurname());
            eventResponseDto.setParticipantCount(event.getParticipants().size());
            return eventResponseDto;
        });

        return response;
    }

    public ResponseEntity getEventById(Long eventId, User sessionUser) {

        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if (optionalEvent.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: The requested event could not be found."
            );
            return ResponseEntity.status(404).body(response);
        }

        Event event = optionalEvent.get();

        List<EventStatus> hiddenStatuses = List.of(
                EventStatus.PLANNED,
                EventStatus.ARCHIVED
        );
        boolean isHiddenStatus = hiddenStatuses.contains(event.getStatus());
        boolean isOwner = event.getOwner().getId().equals(sessionUser.getId());

        if (isHiddenStatus && !isOwner) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: The requested event could not be found."
            );
            return ResponseEntity.status(404).body(response);
        }

        EventResponseDto responseDto = model.map(event, EventResponseDto.class);
        responseDto.setOwnerFullName(event.getOwner().getName() + " " + event.getOwner().getSurname());
        responseDto.setParticipantCount(event.getParticipants().size());

        return ResponseEntity.ok().body(responseDto);
    }

    public ResponseEntity publishEvent(Long eventId, User sessionUser) {

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Event not found",
                    "eventId", eventId
            );
            return ResponseEntity.status(404).body(response);
        }

        Event event = optionalEvent.get();
        if (!event.getOwner().getId().equals(sessionUser.getId())) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Unauthorized: You do not have permission to publish this event.",
                    "eventId", eventId
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (event.getStatus() == EventStatus.PUBLISHED) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: This event is already published.",
                    "eventId", eventId
            );
            return ResponseEntity.badRequest().body(response);
        }

        if (event.getStatus() != EventStatus.PLANNED) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: Only events with 'PLANNED' status can be published.",
                    "eventId", eventId
            );
            return ResponseEntity.badRequest().body(response);
        }

        event.setStatus(EventStatus.PUBLISHED);
        eventRepository.save(event);
        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Event published successfully.",
                "eventId", eventId
        );
        return ResponseEntity.ok(response);
    }

    public Page<EventResponseDto> listMyEvents(int page ,User sessionUser){
        Pageable pageable = Pageable.ofSize(10).withPage(page);
        Page<Event> eventPage = eventRepository.findByOwnerEquals(sessionUser, pageable);
        return eventPage.map(event -> {
            EventResponseDto eventResponseDto = model.map(event, EventResponseDto.class);
            eventResponseDto.setOwnerFullName(event.getOwner().getName() + " " + event.getOwner().getSurname());
            eventResponseDto.setParticipantCount(event.getParticipants().size());
            return eventResponseDto;
        });
    }

    public ResponseEntity cancelEvent(Long eventId, User sessionUser) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Event not found",
                    "eventId", eventId
            );
            return ResponseEntity.status(404).body(response);
        }

        Event event = optionalEvent.get();
        if (!event.getOwner().getId().equals(sessionUser.getId())) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Unauthorized: You do not have permission to cancel this event.",
                    "eventId", eventId
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<EventStatus> cancelableStatuses = List.of(
                EventStatus.PLANNED,
                EventStatus.PUBLISHED,
                EventStatus.ONGOING
        );
        if (event.getStatus() == EventStatus.CANCELED) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "This event has already been canceled.",
                    "eventId", eventId
            );
            return ResponseEntity.badRequest().body(response);
        }

        if (!cancelableStatuses.contains(event.getStatus())) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Cannot cancel this event.",
                    "eventId", eventId
            );
            return ResponseEntity.badRequest().body(response);
        }

        event.setStatus(EventStatus.CANCELED);
        eventRepository.save(event);
        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Event canceled successfully.",
                "eventId", eventId
        );
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity leaveEvent(Long eventId, User sessionUser) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Event not found",
                    "eventId", eventId
            );
            return ResponseEntity.status(404).body(response);
        }

        Event event = optionalEvent.get();
        if (!event.getParticipants().contains(sessionUser)) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "You are not a participant of this event.",
                    "eventId", eventId
            );
            return ResponseEntity.badRequest().body(response);
        }

        if (event.getOwner().getId().equals(sessionUser.getId())) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "As the owner of the event, you cannot leave it. You can cancel the event instead.",
                    "eventId", eventId
            );
            return ResponseEntity.badRequest().body(response);
        }

        event.getParticipants().remove(sessionUser);
        eventRepository.save(event);
        Map<String, Object> response = Map.of(
                "success", true,
                "message", "You have successfully left the event.",
                "eventId", eventId
        );
        return ResponseEntity.ok().body(response);
    }

    public Page<EventResponseDto> listJoinedEvents(int page, User sessionUser) {
        Pageable pageable = Pageable.ofSize(10).withPage(page);
        Page<Event> eventPage = eventRepository.findByParticipantsIn(List.of(sessionUser), pageable);
        return eventPage.map(event -> {
            EventResponseDto eventResponseDto = model.map(event, EventResponseDto.class);
            eventResponseDto.setOwnerFullName(event.getOwner().getName() + " " + event.getOwner().getSurname());
            eventResponseDto.setParticipantCount(event.getParticipants().size());
            return eventResponseDto;
        });
    }

    public Page<EventResponseDto> search(String query, int page, String startDate){
        Sort sort = startDate.equals("desc") ? Sort.by(Sort.Direction.DESC, "startDate") : Sort.by(Sort.Direction.ASC, "startDate");
        Pageable pageable = PageRequest.of(page, 10, sort);

        List<EventStatus> visibleStatuses = List.of(
                EventStatus.PUBLISHED,
                EventStatus.ONGOING
        );

        Page<Event> eventPage = eventRepository.searchVisibleEvents(query, visibleStatuses, pageable);
        return eventPage.map(event -> {
            EventResponseDto eventResponseDto = model.map(event, EventResponseDto.class);
            eventResponseDto.setOwnerFullName(event.getOwner().getName() + " " + event.getOwner().getSurname());
            eventResponseDto.setParticipantCount(event.getParticipants().size());
            return eventResponseDto;
        });
    }

    public ResponseEntity unpublishEvent(Long eventId, User sessionUser){
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Event not found",
                    "eventId", eventId
            );
            return ResponseEntity.status(404).body(response);
        }

        Event event = optionalEvent.get();

        if (event.getParticipants().size() > 1) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error: You cannot unpublish an event that already has participants. Please cancel it instead.",
                    "eventId", eventId
            ));
        }

        if (!event.getOwner().getId().equals(sessionUser.getId())) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Unauthorized: You do not have permission to unpublish this event.",
                    "eventId", eventId
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (event.getStatus() == EventStatus.PLANNED) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: This event is already unpublished.",
                    "eventId", eventId
            );
            return ResponseEntity.badRequest().body(response);
        }

        if (event.getStatus() != EventStatus.PUBLISHED) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Error: Only events with 'PUBLISHED' status can be unpublished.",
                    "eventId", eventId
            );
            return ResponseEntity.badRequest().body(response);
        }

        event.setStatus(EventStatus.PLANNED);
        eventRepository.save(event);
        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Event unpublished successfully.",
                "eventId", eventId
        );
        return ResponseEntity.ok(response);
    }

    public ResponseEntity getEventParticipants(Long eventId, User sessionUser) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Event not found",
                    "eventId", eventId
            );
            return ResponseEntity.status(404).body(response);
        }

        Event event = optionalEvent.get();
        if (!event.getOwner().getId().equals(sessionUser.getId())) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Unauthorized: You do not have permission to view the participants of this event.",
                    "eventId", eventId
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<User> participants = event.getParticipants();

        List<ParticipantResponseDto> participantDtos = participants.stream()
                .map(user -> model.map(user, ParticipantResponseDto.class))
                .toList();

        return ResponseEntity.ok().body(participantDtos);
    }
}
