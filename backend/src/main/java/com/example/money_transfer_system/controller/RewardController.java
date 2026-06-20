package com.example.money_transfer_system.controller;

import com.example.money_transfer_system.entity.Reward;
import com.example.money_transfer_system.security.JwtUtil;
import com.example.money_transfer_system.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
