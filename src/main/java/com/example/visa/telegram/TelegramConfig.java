package com.example.visa.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramConfig {
    private String username;
    private String token;
    private Long creatorId;
}
