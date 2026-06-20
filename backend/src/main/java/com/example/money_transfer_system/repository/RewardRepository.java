package com.example.money_transfer_system.repository;

import com.example.money_transfer_system.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, String> {

    List<Reward> findByAccountIdOrderByCreatedOnDesc(Long accountId);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM Reward r WHERE r.accountId = :accountId")
    Integer sumPointsByAccountId(@Param("accountId") Long accountId);
}
