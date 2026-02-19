package com.example.money_transfer_system.repository;

import com.example.money_transfer_system.entity.Account;
import com.example.money_transfer_system.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Account> findByApprovedFalse();
    
    List<Account> findByStatus(AccountStatus status);
}
