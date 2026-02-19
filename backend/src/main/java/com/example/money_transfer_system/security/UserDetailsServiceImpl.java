package com.example.money_transfer_system.security;

import com.example.money_transfer_system.entity.Account;
import com.example.money_transfer_system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return User.builder()
                .username(account.getEmail())
                .password(account.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(account.getRole().name())))
                .accountLocked(!account.getApproved())
                .disabled(account.getStatus() != com.example.money_transfer_system.enums.AccountStatus.ACTIVE)
                .build();
    }
}
