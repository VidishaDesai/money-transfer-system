package com.example.money_transfer_system.controller;
import java.util.*;
import com.example.money_transfer_system.entity.Account;
import com.example.money_transfer_system.security.JwtUtil;
import com.example.money_transfer_system.service.AccountService;
import com.example.money_transfer_system.service.RewardService;
import com.example.money_transfer_system.dto.AccountSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final RewardService rewardService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Account> getAccount(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        Long authenticatedAccountId = jwtUtil.extractAccountId(token);
        String role = jwtUtil.extractRole(token);

        // Users can only view their own account, admins can view any
        if (!role.equals("ROLE_ADMIN") && !id.equals(authenticatedAccountId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only view your own account");
        }

        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getBalance(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        Long authenticatedAccountId = jwtUtil.extractAccountId(token);
        String role = jwtUtil.extractRole(token);

        // Users can only view their own balance, admins can view any
        if (!role.equals("ROLE_ADMIN") && !id.equals(authenticatedAccountId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only view your own balance");
        }

        BigDecimal balance = accountService.getBalance(id);
        int rewardPoints = rewardService.getTotalPointsForAccount(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("accountId", id);
        response.put("balance", balance);
        response.put("rewardPoints", rewardPoints);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<AccountSearch>> searchAccounts(
            @RequestHeader("Authorization") String authHeader) {

        // Extract token
        String token = authHeader.substring(7);

        // Extract current account id from JWT
        Long authenticatedAccountId = jwtUtil.extractAccountId(token);

        // Fetch filtered accounts
        List<AccountSearch> accounts =
                accountService.getSearchableAccounts(authenticatedAccountId);

        return ResponseEntity.ok(accounts);
    }
}
