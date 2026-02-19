package com.example.money_transfer_system.exception;

public class DuplicateTransferException extends RuntimeException {
    public DuplicateTransferException(String message) {
        super(message);
    }
}
