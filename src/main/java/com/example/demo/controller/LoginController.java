package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private final HttpSessionSecurityContextRepository
            securityContextRepository =
            new HttpSessionSecurityContextRepository();


    public LoginController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

@GetMapping("/csrf")
public ResponseEntity<?> csrf(CsrfToken csrfToken) {
    return ResponseEntity.ok(
        Map.of("token", csrfToken.getToken())
    );
}
    // ==============================
    // LOGIN
    // ==============================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getEmail(),
                                    loginRequest.getPassword()
                            )
                    );


            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);


            securityContextRepository.saveContext(
                    context,
                    request,
                    response
            );


            User user =
                    userRepository
                            .findByEmail(
                                    loginRequest.getEmail()
                            )
                            .orElseThrow();


            Map<String, Object> result =
                    new HashMap<>();

            result.put("id", user.getId());
            result.put("name", user.getName());
            result.put("email", user.getEmail());


            return ResponseEntity.ok(result);

        } catch (AuthenticationException e) {

            return ResponseEntity
                    .status(401)
                    .body("Invalid email or password");
        }
    }


    // ==============================
    // CURRENT USER
    // ==============================

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return ResponseEntity
                    .status(401)
                    .body("Not authenticated");
        }


        User user =
                userRepository
                        .findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow();


        Map<String, Object> result =
                new HashMap<>();

        result.put("id", user.getId());
        result.put("name", user.getName());
        result.put("email", user.getEmail());


        return ResponseEntity.ok(result);
    }
        // ==============================
    // UPDATE PROFILE
    // ==============================

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody ProfileUpdateRequest profileUpdateRequest,
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return ResponseEntity
                    .status(401)
                    .body("Not authenticated");
        }

        String newName = profileUpdateRequest.getName();

if (newName == null ||
        newName.trim().isEmpty() ||
        newName.trim().length() > 50) {

    return ResponseEntity
            .badRequest()
            .body("Name must be between 1 and 50 characters");
}
        User user =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow();

        user.setName(newName.trim());

        userRepository.save(user);

        Map<String, Object> result =
                new HashMap<>();

        result.put("id", user.getId());
        result.put("name", user.getName());
        result.put("email", user.getEmail());

        return ResponseEntity.ok(result);
    }


    // ==============================
    // PROFILE UPDATE REQUEST
    // ==============================

    public static class ProfileUpdateRequest {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    // ==============================
    // LOGIN REQUEST
    // ==============================

    public static class LoginRequest {

        private String email;
        private String password;


        public String getEmail() {
            return email;
        }


        public void setEmail(String email) {
            this.email = email;
        }


        public String getPassword() {
            return password;
        }


        public void setPassword(String password) {
            this.password = password;
        }
    }
}