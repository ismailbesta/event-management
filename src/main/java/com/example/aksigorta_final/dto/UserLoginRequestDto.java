package com.example.aksigorta_final.dto;

import com.example.aksigorta_final.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Data
public class UserLoginRequestDto{
    @NotNull
    @Size(min = 1, max = 200)
    @Email
    @NotEmpty
    String email;
    @NotNull
    @Size(min = 2, max = 1000)
    @NotEmpty
    String password;
}