package com.siteminder.emailservice.provider;

import com.siteminder.emailservice.config.EmailServiceConfig;
import com.siteminder.emailservice.core.FailoverSupport;

/**
 * base interface for an email service provider.
 * will be scanned by spring context and added to the list of supported
 * emial service providers via injection in {@link EmailServiceConfig}
 */
public interface EmailServiceProvider extends FailoverSupport{

}
