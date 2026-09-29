package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==========================================
    // REGISTER USER
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody User user) {

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Email already registered");
        }

        User savedUser =
                userService.registerUser(user);

        return ResponseEntity.ok(
                new UserResponse(
                        savedUser.getId(),
                        savedUser.getName(),
                        savedUser.getEmail()
                )
        );
    }

    // ==========================================
    // USER RESPONSE DTO
    // ==========================================

    public static class UserResponse {

        private Long id;
        private String name;
        private String email;

        public UserResponse(
                Long id,
                String name,
                String email) {

            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}