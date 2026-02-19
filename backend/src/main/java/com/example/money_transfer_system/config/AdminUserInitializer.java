package com.example.money_transfer_system.config;

import com.example.money_transfer_system.entity.Account;
import com.example.money_transfer_system.enums.AccountStatus;
import com.example.money_transfer_system.enums.Role;
import com.example.money_transfer_system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.money_transfer_system.enums.AccountType;

import java.math.BigDecimal;

/**
 * Initializes the database with a default admin user on application startup.
 * This runs only if no admin user exists in the database.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    @Value("${admin.default.name:System Administrator}")
    private String adminName;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (accountRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Admin user already exists: {}", adminEmail);
            return;
        }

        // Create admin user
        Account admin = new Account();
        admin.setHolderName(adminName);
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setBalance(BigDecimal.ZERO);
        admin.setStatus(AccountStatus.ACTIVE);
        admin.setApproved(true);
        admin.setRole(Role.ROLE_ADMIN);
        admin.setAccountType(AccountType.ADMIN);


        accountRepository.save(admin);

        log.info("==============================================");
        log.info("DEFAULT ADMIN USER CREATED");
        log.info("Email: {}", adminEmail);
        log.info("==============================================");
        log.info("IMPORTANT: Change the admin password after first login!");
        log.info("==============================================");
    }
}
