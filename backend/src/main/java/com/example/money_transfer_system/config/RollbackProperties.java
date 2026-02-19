package com.example.money_transfer_system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rollback")
@Getter
@Setter
public class RollbackProperties {

    private int windowMinutes;
}
