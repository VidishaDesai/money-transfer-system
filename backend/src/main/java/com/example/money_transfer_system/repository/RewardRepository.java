package com.example.money_transfer_system.repository;

import com.example.money_transfer_system.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;  //
import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, String> {

    List<Reward> findByAccountIdOrderByCreatedOnDesc(Long accountId);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM Reward r WHERE r.accountId = :accountId")
    Integer sumPointsByAccountId(@Param("accountId") Long accountId);

    //added
    long countByAccountIdAndCreatedOnGreaterThanEqual(Long accountId, LocalDateTime startOfDay);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM Reward r WHERE r.accountId = :accountId AND r.createdOn >= :startOfDay")
    Integer sumPointsByAccountIdSince(@Param("accountId") Long accountId, @Param("startOfDay") LocalDateTime startOfDay);

    // Per sender-recipient pair counters
    @Query("SELECT COUNT(r) FROM Reward r WHERE r.accountId = :accountId AND r.toAccountId = :toAccountId AND r.createdOn >= :startOfDay")
    long countByAccountIdAndToAccountIdAndCreatedOnGreaterThanEqual(@Param("accountId") Long accountId, @Param("toAccountId") Long toAccountId, @Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM Reward r WHERE r.accountId = :accountId AND r.toAccountId = :toAccountId AND r.createdOn >= :startOfDay")
    Integer sumPointsByAccountIdAndToAccountIdSince(@Param("accountId") Long accountId, @Param("toAccountId") Long toAccountId, @Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM Reward r")
    Integer sumAllPoints();
}
