package com.example.aksigorta_final.service;

import com.example.aksigorta_final.dto.UserLoginRequestDto;
import com.example.aksigorta_final.dto.UserRegisterRequestDto;
import com.example.aksigorta_final.dto.UserResponseDto;
import com.example.aksigorta_final.entity.User;
import com.example.aksigorta_final.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper model;
    private final HttpServletRequest request;

    public ResponseEntity register(UserRegisterRequestDto userRegisterRequestDto) {
        User user = userRepository.findByEmailEqualsIgnoreCase(userRegisterRequestDto.getEmail());
        if (user != null) {
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "This email is already registered."
            );
            return ResponseEntity.badRequest().body(response);
        }
        User newUser = model.map(userRegisterRequestDto, User.class);
        String encodedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
        newUser.setPassword(encodedPassword);
        userRepository.save(newUser);
        UserResponseDto userResponseDto = model.map(newUser, UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
    }

    public ResponseEntity Login(UserLoginRequestDto userLoginRequestDto){
        User user = userRepository.findByEmailEqualsIgnoreCase(userLoginRequestDto.getEmail());
        if (user != null) {
            boolean isMatch = BCrypt.checkpw(userLoginRequestDto.getPassword(), user.getPassword());
            if (isMatch) {
                UserResponseDto userResponseDto = model.map(user, UserResponseDto.class);
                request.getSession().setAttribute("user", userResponseDto);
                return ResponseEntity.ok(userResponseDto);
            }
        }
        Map<String, Object> response = Map.of(
                "success", false,
                "message", "Email or password is incorrect."
        );
        return ResponseEntity.badRequest().body(response);
    }

    public ResponseEntity logout(){
        request.getSession().invalidate();
        Map<String, Object> hm = Map.of("success", true, "message", "Logout successful.");
        return ResponseEntity.ok().body(hm);
    }
}
