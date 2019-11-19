package com.siteminder.emailservice.core;

import com.siteminder.emailservice.model.Exchange;

/**
 * Functional callback interface for {@link FailoverEmailRouter}
 * for example a {@link FailoverEmailRouter} should invoke the done method when
 * the {@link Exchange} is ready to be continued to be routed. This would allow
 * to build non blocking request/reply communication.
 */
public interface FailoverCallback {
    void done();
}
