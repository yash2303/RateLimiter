package com.yashasvi;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class APIRateLimiterSlidingCounterUsingQueue implements APIRateLimiter {
    private final int duration; // Seconds
    private final Long requestLimit;
    private final ConcurrentLinkedDeque<TimestampCounter> queue;

    public APIRateLimiterSlidingCounterUsingQueue(int duration, Long requestLimit) {
        this.duration = duration;
        this.requestLimit = requestLimit;
        this.queue = new ConcurrentLinkedDeque<>();
    }

    @Override
    public boolean isAccepted(Date timestamp) {
        long epochTimeInSeconds = timestamp.getTime() / 1000;
        System.out.printf("Request received at timestamp: %s%n", timestamp);
        while (!queue.isEmpty() && queue.getFirst().getTimestamp() <= epochTimeInSeconds - duration) {
            queue.pollFirst();
        }
        long requestCount = getRequestCount();
        if (requestCount >= requestLimit)
            return false;
        incrementCount(epochTimeInSeconds);
        return true;
    }

    private long getRequestCount() {
        int requestCount = 0;
        for (TimestampCounter timestampCounter : queue) {
            requestCount += timestampCounter.getCounter().get();
        }
        System.out.printf("Request count in last %s seconds: %s%n", duration, requestCount);
        return requestCount;
    }

    private void incrementCount(long epochTimeInSeconds) {
        if (!queue.isEmpty() && queue.getLast().getTimestamp() == epochTimeInSeconds) {
            queue.getLast().getCounter().getAndIncrement();
        } else {
            queue.addLast(new TimestampCounter(epochTimeInSeconds, new AtomicInteger(1)));
        }
    }
}
