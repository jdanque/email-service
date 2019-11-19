package com.siteminder.emailservice.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "email-service")
public class EmailServiceProviderConfig {

    private Map<String, EmailServiceProviderProperties> providersConfig;

    public Map<String, EmailServiceProviderProperties> getProvidersConfig() {
        return providersConfig;
    }

    public void setProvidersConfig(Map<String, EmailServiceProviderProperties> providersConfig) {
        this.providersConfig = providersConfig;
    }
}
