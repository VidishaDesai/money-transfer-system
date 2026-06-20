package com.example.money_transfer_system.controller;

import com.example.money_transfer_system.dto.RewardRedeemRequest;
import com.example.money_transfer_system.entity.Reward;
import com.example.money_transfer_system.entity.RewardRedemption;
import com.example.money_transfer_system.security.JwtUtil;
import com.example.money_transfer_system.service.RewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final JwtUtil jwtUtil;

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<Reward>> getRewardHistory(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long accountId = jwtUtil.extractAccountId(token);

        List<Reward> rewards = rewardService.getRewardHistory(accountId);
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Map<String, Object>> getBalance(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long accountId = jwtUtil.extractAccountId(token);

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", accountId);
        response.put("balance", rewardService.getTotalPointsForAccount(accountId));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Map<String, Object>> redeemPoints(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RewardRedeemRequest request) {

        String token = authHeader.substring(7);
        Long accountId = jwtUtil.extractAccountId(token);

        RewardRedemption redemption = rewardService.redeemPoints(
                accountId, request.getPoints(), request.getDescription());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("redemptionId", redemption.getId());
        response.put("pointsRedeemed", redemption.getPoints());
        response.put("remainingBalance", rewardService.getTotalPointsForAccount(accountId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/redemptions")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<RewardRedemption>> getRedemptionHistory(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long accountId = jwtUtil.extractAccountId(token);

        return ResponseEntity.ok(rewardService.getRedemptionHistory(accountId));
    }
}