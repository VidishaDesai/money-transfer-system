package com.example.money_transfer_system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.reward")
@Getter
@Setter
public class RewardProperties {

    // Max number of transfers that can earn reward points, per account, per day
    private int maxRewardedTransfersPerDay = 5;

    // Max total reward points an account can earn per day (across all transfers)
    private int maxPointsPerDay = 50;
}