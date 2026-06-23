package com.example.visa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "site")
public class SiteConfig {
    private String url;
    private String siteKey;
    private String captchaApiKey;
}
