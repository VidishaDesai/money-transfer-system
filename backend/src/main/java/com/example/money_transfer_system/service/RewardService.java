package com.example.money_transfer_system.service;

import com.example.money_transfer_system.entity.Reward;
import com.example.money_transfer_system.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final RewardRepository rewardRepository;

    public int awardReward(Long accountId, String transactionId, BigDecimal amount) {
        if (accountId == null || transactionId == null || amount == null) {
            return 0;
        }

        if (amount.compareTo(new BigDecimal("100")) <= 0) {
            return 0;
        }

        int points = amount.divideToIntegralValue(new BigDecimal("100")).intValue();
        if (points <= 0) {
            return 0;
        }

        Reward reward = new Reward();
        reward.setAccountId(accountId);
        reward.setTransactionId(transactionId);
        reward.setPoints(points);
        reward.setAmount(amount);

        rewardRepository.save(reward);

        log.info("Awarded {} reward points for account {} on transaction {}", points, accountId, transactionId);

        return points;
    }

    public List<Reward> getRewardHistory(Long accountId) {
        return rewardRepository.findByAccountIdOrderByCreatedOnDesc(accountId);
    }

    public int getTotalPointsForAccount(Long accountId) {
        Integer total = rewardRepository.sumPointsByAccountId(accountId);
        return total == null ? 0 : total;
    }
}
