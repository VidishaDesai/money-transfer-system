package com.example.money_transfer_system.service;

import com.example.money_transfer_system.dto.LoginRequest;
import com.example.money_transfer_system.dto.LoginResponse;
import com.example.money_transfer_system.dto.RegisterRequest;
import com.example.money_transfer_system.entity.Account;
import com.example.money_transfer_system.enums.AccountStatus;
import com.example.money_transfer_system.exception.AccountNotActiveException;
import com.example.money_transfer_system.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public Account register(RegisterRequest request) {

        Account account = accountService.registerAccount(request);

        log.info("User registered successfully: {}", request.getEmail());

        return account;
    }



    public LoginResponse login(LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            Account account = accountService.getAccountByEmail(request.getEmail());

            // 🔒 Check approval
            if (!account.getApproved()) {
                throw new AccountNotActiveException("Account is pending admin approval");
            }

            // 🔒 Check status
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new AccountNotActiveException("Account is not active");
            }

            String token = jwtUtil.generateToken(
                    account.getEmail(),
                    account.getId(),
                    account.getRole().name()
            );

            log.info("User logged in successfully: {}", request.getEmail());

            return new LoginResponse(
                    token,
                    account.getId(),
                    account.getHolderName(),
                    account.getEmail(),
                    account.getRole().name(),
                    account.getBalance()
            );

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}
