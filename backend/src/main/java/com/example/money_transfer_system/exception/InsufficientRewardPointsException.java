package com.example.money_transfer_system.exception;

public class InsufficientRewardPointsException extends RuntimeException {
    public InsufficientRewardPointsException(String message) {
        super(message);
    }
}