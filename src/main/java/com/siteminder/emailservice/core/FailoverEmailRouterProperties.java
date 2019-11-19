package com.siteminder.emailservice.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * main properties for the failover bean
 * @see {@link FailoverEmailRouter}
 */
@Configuration
@ConfigurationProperties(prefix = "email-service.failover")
public class FailoverEmailRouterProperties {

    private boolean sticky = true;
    private int maxAttempts = -1;

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
