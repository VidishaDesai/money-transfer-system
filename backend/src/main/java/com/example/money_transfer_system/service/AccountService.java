package com.example.money_transfer_system.service;

import com.example.money_transfer_system.config.AccountProperties;
import com.example.money_transfer_system.dto.DepositRequest;

import com.example.money_transfer_system.dto.AccountSearch;
import com.example.money_transfer_system.dto.RegisterRequest;
import com.example.money_transfer_system.entity.Account;
import com.example.money_transfer_system.entity.TransactionLog;
import com.example.money_transfer_system.enums.AccountStatus;
import com.example.money_transfer_system.enums.Role;
import com.example.money_transfer_system.enums.TransactionStatus;
import com.example.money_transfer_system.enums.TransactionType;
import com.example.money_transfer_system.exception.AccountNotFoundException;
import com.example.money_transfer_system.exception.InvalidAmountException;
import com.example.money_transfer_system.repository.AccountRepository;
import com.example.money_transfer_system.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.money_transfer_system.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionLogRepository transactionLogRepository;
    private final PasswordEncoder passwordEncoder;

    private final AccountProperties accountProperties;

    @Transactional
    public Account registerAccount(RegisterRequest registerRequest) {

        if (accountRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Account account = new Account();
        account.setHolderName(registerRequest.getHolderName());
        account.setEmail(registerRequest.getEmail());
        account.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.LOCKED);
        account.setApproved(false);
        account.setRole(Role.ROLE_USER);
        account.setAccountType(registerRequest.getAccountType());

        // NEW PROFILE FIELDS
        account.setPhone(registerRequest.getPhone());
        account.setAddress(registerRequest.getAddress());
        account.setDateOfBirth(registerRequest.getDateOfBirth());

        return accountRepository.save(account);
    }


    @Transactional
    public Account approveAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setApproved(true);
        account.setStatus(AccountStatus.ACTIVE);

        BigDecimal minimumBalance = accountProperties
                .getMinimumBalance(account.getAccountType().name());

        account.setBalance(minimumBalance);

        return accountRepository.save(account);
    }


    @Transactional
    public Account rejectAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        account.setStatus(AccountStatus.CLOSED);
        Account rejected = accountRepository.save(account);
        log.info("Account rejected: {} (ID: {})", account.getEmail(), accountId);
        return rejected;
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
    }

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with email: " + email));
    }

    public List<Account> getPendingAccounts() {
        return accountRepository.findByApprovedFalse();
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public void deposit(DepositRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }

        Account account = getAccountById(request.getAccountId());
        
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        // Log deposit transaction
        TransactionLog log = new TransactionLog();
        log.setToAccountId(account.getId());
        log.setAmount(request.getAmount());
        log.setTransactionType(TransactionType.DEPOSIT);
        log.setStatus(TransactionStatus.SUCCESS);
        transactionLogRepository.save(log);

        this.log.info("Deposited {} to account ID: {}", request.getAmount(), request.getAccountId());
    }

    public BigDecimal getBalance(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getBalance();
    }

    public List<AccountSearch> getSearchableAccounts(Long currentAccountId) {

        return accountRepository.findAll()
                .stream()
                .filter(acc -> !acc.getId().equals(1L)) // exclude system admin
                .filter(acc -> !acc.getId().equals(currentAccountId)) // exclude self
                .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE) // only active accounts

                .map(acc -> new AccountSearch(
                        acc.getId(),
                        acc.getHolderName()
                ))
                .toList();
    }
}
