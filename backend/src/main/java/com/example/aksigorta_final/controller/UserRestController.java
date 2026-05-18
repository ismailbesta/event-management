package com.example.aksigorta_final.controller;

import com.example.aksigorta_final.dto.UserLoginRequestDto;
import com.example.aksigorta_final.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.aksigorta_final.dto.UserRegisterRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Tag(name = "User", description = "Authentication and user session operations")
public class UserRestController {

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity register(@Valid @RequestBody UserRegisterRequestDto userRegisterRequestDto){
        return userService.register(userRegisterRequestDto);
    }

    @PostMapping("login")
    public ResponseEntity login(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto){
        return userService.login(userLoginRequestDto);
    }

    @PostMapping("logout")
    public ResponseEntity logout(){
        return userService.logout();
    }
}
