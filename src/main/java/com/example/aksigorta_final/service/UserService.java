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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper model;
    private final HttpServletRequest request;

    public ResponseEntity register(UserRegisterRequestDto userRegisterRequestDto) {
        Optional<User> optionalUser = userRepository.findByEmailEqualsIgnoreCase(userRegisterRequestDto.getEmail());
        if (optionalUser.isPresent()) {
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

    public ResponseEntity login(UserLoginRequestDto userLoginRequestDto){
        Optional<User> optionalUser = userRepository.findByEmailEqualsIgnoreCase(userLoginRequestDto.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean isMatch = BCrypt.checkpw(userLoginRequestDto.getPassword(), user.getPassword());
            if (isMatch) {
                request.getSession().setAttribute("user", user);
                UserResponseDto userResponseDto = model.map(user, UserResponseDto.class);
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
        Map<String, Object> hm = Map.of(
                "success", true,
                "message", "Logout successful."
        );
        return ResponseEntity.ok().body(hm);
    }
}
