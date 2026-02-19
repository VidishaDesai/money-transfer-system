package com.example.money_transfer_system.controller;

import com.example.money_transfer_system.dto.LoginRequest;
import com.example.money_transfer_system.dto.LoginResponse;
import com.example.money_transfer_system.dto.RegisterRequest;
import com.example.money_transfer_system.entity.Account;
import com.example.money_transfer_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Account account = authService.register(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Registration successful! Your account is pending admin approval.");
        response.put("accountId", account.getId());
        response.put("email", account.getEmail());
        response.put("status", account.getStatus());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
