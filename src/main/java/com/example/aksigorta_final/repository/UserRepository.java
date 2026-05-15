package com.example.aksigorta_final.repository;

import com.example.aksigorta_final.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailEqualsIgnoreCase(String email);
}