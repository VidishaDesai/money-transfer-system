package com.example.money_transfer_system.entity;

import com.example.money_transfer_system.enums.TransactionStatus;
import com.example.money_transfer_system.enums.TransactionType;
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
@Table(name = "transaction_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLog {

    //@Id
    //@Column(name = "transaction_id", length = 36, updatable = false)
    //private String id = UUID.randomUUID().toString();

    @Setter(AccessLevel.NONE)
    @Id
    @Column(name = "transaction_id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "from_account_id")
    private Long fromAccountId;

    @Column(name = "to_account_id")
    private Long toAccountId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.SUCCESS;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "original_transaction_id")
    private String originalTransactionId;

    @Column(name = "rollback_requested_at")
    private LocalDateTime rollbackRequestedAt;

    @Column(name = "rollback_processed_at")
    private LocalDateTime rollbackProcessedAt;

    @Column(name = "rollback_processed_by")
    private Long rollbackProcessedBy;


    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
