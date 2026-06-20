package com.example.money_transfer_system.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reward_redemptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardRedemption {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(name = "redemption_id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "description", length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "redeemed_on", updatable = false)
    private LocalDateTime redeemedOn;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}