package com.example.money_transfer_system.repository;

import com.example.money_transfer_system.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.money_transfer_system.enums.TransactionStatus;

import java.util.List;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, String> {
    
    boolean existsByIdempotencyKey(String idempotencyKey);
    boolean existsByOriginalTransactionId(String originalTransactionId);

    // @Query("SELECT t FROM TransactionLog t WHERE t.fromAccountId = :accountId OR t.toAccountId = :accountId ORDER BY t.createdOn DESC")
    @Query("SELECT t FROM TransactionLog t " +
       "WHERE (t.fromAccountId = :accountId AND t.transactionType = 'DEBIT') " +
       "OR (t.toAccountId = :accountId AND t.transactionType = 'CREDIT') " +
       "ORDER BY t.createdOn DESC")
    List<TransactionLog> findByAccountId(@Param("accountId") Long accountId);
    
    List<TransactionLog> findAllByOrderByCreatedOnDesc();

    List<TransactionLog> findByStatus(TransactionStatus status);

}
