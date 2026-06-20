package com.example.money_transfer_system.repository;

import com.example.money_transfer_system.entity.RewardRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRedemptionRepository extends JpaRepository<RewardRedemption, String> {

    List<RewardRedemption> findByAccountIdOrderByRedeemedOnDesc(Long accountId);

    List<RewardRedemption> findAllByOrderByRedeemedOnDesc();

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM RewardRedemption r WHERE r.accountId = :accountId")
    Integer sumPointsByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM RewardRedemption r")
    Integer sumAllPoints();
}