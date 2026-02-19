package com.example.money_transfer_system.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for seed data.
 * Run this class with passwords as command-line arguments.
 * <p>
 * Usage: {@code java PasswordHashGenerator <password1> [password2] ...}
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java PasswordHashGenerator <password1> [password2] ...");
            System.out.println("Example: java PasswordHashGenerator mySecretPass anotherPass");
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("=== Password Hash Generator ===\n");

        for (String password : args) {
            String hash = encoder.encode(password);
            System.out.println("BCrypt Hash: " + hash);
            System.out.println();
        }

        System.out.println("Copy these hashes to your database/seed-data.sql file.");
    }
}
