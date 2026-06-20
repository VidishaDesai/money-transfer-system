package com.example.money_transfer_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RewardAdminSummary {
    private long totalPointsIssued;
    private long totalPointsRedeemed;
    private long totalPointsOutstanding;
}