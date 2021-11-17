package com.yashasvi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class APIRateLimiterWithSlidingCountersUsingFixedArray implements APIRateLimiter {
    private final int duration; // Seconds
    private final Long requestLimit;
    private final List<Long> hits;
    private final List<Long> times;

    public APIRateLimiterWithSlidingCountersUsingFixedArray(int duration, Long requestLimit) {
        this.duration = duration;
        this.requestLimit = requestLimit;
        this.hits = new ArrayList<>(Collections.nCopies(duration, 0L));
        this.times = new ArrayList<>(Collections.nCopies(duration, 0L));
        System.out.printf("hits size: %s%n%n", hits.size());
    }

    @Override
    public boolean isAccepted(Date timestamp) {
        long epochTimeInSeconds = timestamp.getTime() / 1000;
        System.out.printf("Request received at timestamp: %s%n", timestamp);
        long requestCount = getRequestCount(epochTimeInSeconds);
        if (requestCount >= requestLimit)
            return false;
        incrementCount(epochTimeInSeconds);
        return true;
    }

    private long getRequestCount(long epochTimeInSeconds) {
        int requestCount = 0;
        for (int i = 0; i < duration; i++) {
            if (epochTimeInSeconds - times.get(i) < duration) {
                requestCount += hits.get(i);
            }
        }
        System.out.printf("Request count in last %s seconds: %s%n", duration, requestCount);
        return requestCount;
    }

    private void incrementCount(long epochTimeInSeconds) {
        int idx = (int) (epochTimeInSeconds % duration);
        if (times.get(idx) != epochTimeInSeconds) {
            times.set(idx, epochTimeInSeconds);
            hits.set(idx, 1L);
        } else {
            hits.set(idx, hits.get(idx) + 1);
        }
    }
}
