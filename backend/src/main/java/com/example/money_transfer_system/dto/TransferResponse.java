package com.example.money_transfer_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String transactionId;
    private String status;
    private String message;
    private Long debitedFrom;
    private Long creditedTo;
    private BigDecimal amount;
}
