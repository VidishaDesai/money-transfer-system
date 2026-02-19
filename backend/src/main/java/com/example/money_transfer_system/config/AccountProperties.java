package com.example.money_transfer_system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.account")
public class AccountProperties {

    private Map<String, BigDecimal> minimumBalances;

    public Map<String, BigDecimal> getMinimumBalances() {
        return minimumBalances;
    }

    public void setMinimumBalances(Map<String, BigDecimal> minimumBalances) {
        this.minimumBalances = minimumBalances;
    }

    public BigDecimal getMinimumBalance(String accountType) {
        BigDecimal balance = minimumBalances.get(accountType);
        if (balance == null) {
            throw new IllegalArgumentException("Minimum balance not configured for account type: " + accountType);
        }
        return balance;
    }
}
