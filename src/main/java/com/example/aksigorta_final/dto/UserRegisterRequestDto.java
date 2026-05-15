package com.example.aksigorta_final.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequestDto {

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @Size(min = 1, max = 100)
    private String surname;

    @NotBlank
    @Email
    @Size(min = 1, max = 200)
    private String email;

    @NotBlank
    @Size(min = 6, max = 1000)
    private String password;
}