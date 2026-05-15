package com.example.aksigorta_final.controller;

import com.example.aksigorta_final.dto.EventCreateRequestDto;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("event")
public class EventRestController {
    private final EventService eventService;

    public ResponseEntity createEvent(EventCreateRequestDto eventCreateRequestDto, HttpServletRequest request){
        User sessionUser = (User) request.getSession().getAttribute("user");
        return eventService.createEvent(eventCreateRequestDto, sessionUser);
    }
}
