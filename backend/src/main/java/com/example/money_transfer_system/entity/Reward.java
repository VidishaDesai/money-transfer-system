package com.example.money_transfer_system.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(name = "reward_id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "transaction_id", length = 36, nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
