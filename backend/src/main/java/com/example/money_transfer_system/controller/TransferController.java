package com.example.money_transfer_system.controller;

import com.example.money_transfer_system.dto.TransferRequest;
import com.example.money_transfer_system.dto.TransferResponse;
import com.example.money_transfer_system.entity.TransactionLog;
import com.example.money_transfer_system.security.JwtUtil;
import com.example.money_transfer_system.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        // Extract account ID from JWT
        String token = authHeader.substring(7);
        Long authenticatedAccountId = jwtUtil.extractAccountId(token);
        String role = jwtUtil.extractRole(token);

        // Non-admin users can only transfer from their own account
        if (!role.equals("ROLE_ADMIN") && !request.getFromAccountId().equals(authenticatedAccountId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only transfer from your own account");
        }

        TransferResponse response = transferService.transfer(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TransactionLog>> getTransactionHistory(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        Long accountId = jwtUtil.extractAccountId(token);

        List<TransactionLog> history = transferService.getTransactionHistory(accountId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{transactionId}/rollback")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> requestRollback(
            @PathVariable String transactionId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long accountId = jwtUtil.extractAccountId(token);

        transferService.requestRollback(transactionId, accountId);

        return ResponseEntity.ok("Rollback request submitted successfully");
    }

}
