package com.yashasvi;

import java.util.Date;

public interface APIRateLimiter {
    boolean isAccepted(Date timestamp);
}
