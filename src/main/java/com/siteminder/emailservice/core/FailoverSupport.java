package com.siteminder.emailservice.core;

import com.siteminder.emailservice.model.Exchange;

/**
 * Base class for classes that could process a failover
 * @see {@link FailoverEmailRouter}
 */
public interface FailoverSupport {

    void process(Exchange exchange, FailoverCallback callback);
}
