package com.example.money_transfer_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RewardAccountSummary {
    private Long accountId;
    private String holderName;
    private String email;
    private int totalEarned;
    private int totalRedeemed;
    private int balance;
}