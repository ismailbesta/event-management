package com.example.aksigorta_final.dto;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.aksigorta_final.entity.User}
 */
@Data
public class UserResponseDto{
    Long id;
    String name;
    String surname;
    String email;
}