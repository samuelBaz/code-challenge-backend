package com.example.backend.controllers;

import com.example.backend.database.models.User;
import com.example.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String email, @RequestParam String password) {
        User user = authService.registerUser(email, password);
        return ResponseEntity.ok("User registered successfully: " + user.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        User user = authService.loginUser(email, password);
        return ResponseEntity.ok("Welcome, " + user.getEmail());
    }
}