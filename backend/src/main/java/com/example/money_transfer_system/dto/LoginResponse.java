package com.example.money_transfer_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long accountId;
    private String holderName;
    private String email;
    private String role;
    private BigDecimal balance;
}
