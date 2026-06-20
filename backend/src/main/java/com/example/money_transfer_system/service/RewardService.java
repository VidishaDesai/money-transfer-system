package com.example.money_transfer_system.service;

import com.example.money_transfer_system.config.RewardProperties;
import com.example.money_transfer_system.dto.RewardAccountSummary;
import com.example.money_transfer_system.dto.RewardAdminSummary;
import com.example.money_transfer_system.entity.Account;
import com.example.money_transfer_system.entity.Reward;
import com.example.money_transfer_system.entity.RewardRedemption;
import com.example.money_transfer_system.exception.InsufficientRewardPointsException;
import com.example.money_transfer_system.repository.AccountRepository;
import com.example.money_transfer_system.repository.RewardRedemptionRepository;
import com.example.money_transfer_system.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository rewardRedemptionRepository;
    private final RewardProperties rewardProperties;
    private final AccountRepository accountRepository;

    @Transactional
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

        // ===== Daily rate limiting (anti-gaming) =====
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();

        long rewardedTransfersToday = rewardRepository
                .countByAccountIdAndCreatedOnGreaterThanEqual(accountId, startOfDay);

        if (rewardedTransfersToday >= rewardProperties.getMaxRewardedTransfersPerDay()) {
            log.info("Daily rewarded-transfer cap reached for account {} ({} transfers today)",
                    accountId, rewardedTransfersToday);
            return 0;
        }

        Integer pointsEarnedToday = rewardRepository.sumPointsByAccountIdSince(accountId, startOfDay);
        int alreadyEarnedToday = pointsEarnedToday == null ? 0 : pointsEarnedToday;

        int remainingDailyAllowance = rewardProperties.getMaxPointsPerDay() - alreadyEarnedToday;
        if (remainingDailyAllowance <= 0) {
            log.info("Daily points cap reached for account {} ({} points today)",
                    accountId, alreadyEarnedToday);
            return 0;
        }

        // Cap to remaining daily allowance instead of denying outright
        points = Math.min(points, remainingDailyAllowance);

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

    /**
     * Returns the account's CURRENT usable reward balance
     * (total points earned minus total points already redeemed).
     * Existing callers (AuthService, AccountController) keep working unchanged,
     * but now correctly reflect redemptions.
     */
    public int getTotalPointsForAccount(Long accountId) {
        return getAvailableBalance(accountId);
    }

    private int getEarnedPoints(Long accountId) {
        Integer total = rewardRepository.sumPointsByAccountId(accountId);
        return total == null ? 0 : total;
    }

    private int getRedeemedPoints(Long accountId) {
        Integer total = rewardRedemptionRepository.sumPointsByAccountId(accountId);
        return total == null ? 0 : total;
    }

    private int getAvailableBalance(Long accountId) {
        int balance = getEarnedPoints(accountId) - getRedeemedPoints(accountId);
        return Math.max(0, balance);
    }

    // ===== Redemption =====

    @Transactional
    public RewardRedemption redeemPoints(Long accountId, int pointsToRedeem, String description) {
        if (pointsToRedeem <= 0) {
            throw new InsufficientRewardPointsException("Points to redeem must be greater than zero");
        }

        int availableBalance = getAvailableBalance(accountId);
        if (pointsToRedeem > availableBalance) {
            throw new InsufficientRewardPointsException(
                    "Insufficient reward points. Available balance: " + availableBalance);
        }

        RewardRedemption redemption = new RewardRedemption();
        redemption.setAccountId(accountId);
        redemption.setPoints(pointsToRedeem);
        redemption.setDescription(
                (description == null || description.isBlank()) ? "Redeemed for offers" : description);

        RewardRedemption saved = rewardRedemptionRepository.save(redemption);

        log.info("Account {} redeemed {} reward points ({} remaining)",
                accountId, pointsToRedeem, availableBalance - pointsToRedeem);

        return saved;
    }

    public List<RewardRedemption> getRedemptionHistory(Long accountId) {
        return rewardRedemptionRepository.findByAccountIdOrderByRedeemedOnDesc(accountId);
    }

    // ===== Admin visibility =====

    public RewardAdminSummary getAdminSummary() {
        Integer issuedObj = rewardRepository.sumAllPoints();
        Integer redeemedObj = rewardRedemptionRepository.sumAllPoints();
        long issued = issuedObj == null ? 0 : issuedObj;
        long redeemed = redeemedObj == null ? 0 : redeemedObj;
        return new RewardAdminSummary(issued, redeemed, issued - redeemed);
    }

    public List<RewardAccountSummary> getAccountSummaries() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(account -> {
                    int earned = getEarnedPoints(account.getId());
                    int redeemed = getRedeemedPoints(account.getId());
                    return new RewardAccountSummary(
                            account.getId(),
                            account.getHolderName(),
                            account.getEmail(),
                            earned,
                            redeemed,
                            Math.max(0, earned - redeemed)
                    );
                })
                .filter(summary -> summary.getTotalEarned() > 0 || summary.getTotalRedeemed() > 0)
                .toList();
    }

    public List<RewardRedemption> getAllRedemptions() {
        return rewardRedemptionRepository.findAllByOrderByRedeemedOnDesc();
    }
}