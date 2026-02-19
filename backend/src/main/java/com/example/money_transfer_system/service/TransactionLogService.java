// src/main/java/com/example/money_transfer_system/service/TransactionLogService.java
package com.example.money_transfer_system.service;

import com.example.money_transfer_system.entity.TransactionLog;
import com.example.money_transfer_system.enums.TransactionStatus;
import com.example.money_transfer_system.enums.TransactionType;
import com.example.money_transfer_system.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final TransactionLogRepository repo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(Long fromId, Long toId, BigDecimal amount, TransactionType type,
                           String idempotencyKey, String reason) {
        TransactionLog log = new TransactionLog();
        // If your entity uses a UUID/auto id, set accordingly. If DB generates ID, skip explicit ID.
        // log.setId(UUID.randomUUID().toString()); // only if you manage IDs yourself

        log.setFromAccountId(fromId);
        log.setToAccountId(toId);
        log.setAmount(amount);
        log.setTransactionType(type);              // e.g., TransactionType.TRANSFER
        log.setStatus(TransactionStatus.FAILED);   // FAILED
        log.setFailureReason(trim(reason, 500));   // your schema has VARCHAR(500)
        log.setIdempotencyKey(safeKey(idempotencyKey, "-FAIL")); // avoid unique key clashes

        repo.save(log);
    }

    private String trim(String s, int max) {
        return s == null ? null : (s.length() > max ? s.substring(0, max) : s);
    }

    private String safeKey(String key, String suffix) {
        if (key == null) return null;
        String v = key + suffix;  // e.g., ABC123-FAIL
        return v.length() > 100 ? v.substring(0, 100) : v; // your column is VARCHAR(100)
    }
}