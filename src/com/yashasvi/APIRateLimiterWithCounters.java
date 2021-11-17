package com.yashasvi;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APIRateLimiterWithCounters implements APIRateLimiter {
    private final ConcurrentHashMap<Long, Long> requestCountPerSecond;  // Second, Count
    private final int duration; // Seconds
    private final Long requestLimit;
    private final Long cleanUpPeriod = 30L * 1000; // can be taken as part of input
    private final ExecutorService executorService;

    public APIRateLimiterWithCounters(int duration, Long requestLimit) {
        this.duration = duration;
        this.requestLimit = requestLimit;
        this.requestCountPerSecond = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    private Thread getCleanUpThread(Long currentTimestamp) {
        return new Thread(() -> {
            System.out.printf("Executing clean up at timestamp: %s%n", new Date());
            for (Long timestamp : requestCountPerSecond.keySet()) {
                if (timestamp < currentTimestamp - duration) {
                    System.out.printf("Removing timestamp: %s%n", timestamp);
                    requestCountPerSecond.remove(timestamp);
                }
            }
            System.out.printf("Clean up executed. Current size: %s%n%n", requestCountPerSecond.size());
        });
    }

    @Override
    public boolean isAccepted(Date timestamp) {
        long epochTimeInSeconds = 0;
        try {
            System.out.printf("Request received at timestamp: %s%n", timestamp);
            epochTimeInSeconds = timestamp.getTime() / 1000;
            long requestCount = getRequestCount(epochTimeInSeconds);
            if (requestCount >= requestLimit) {
                return false;
            }
            incrementCount(epochTimeInSeconds);
            return true;
        } finally {
            Thread cleanUp = getCleanUpThread(epochTimeInSeconds);
            executorService.submit(cleanUp);
        }
    }

    private long getRequestCount(long epochTimeInSeconds) {
        long requestCount = 0;
        for (long seconds = 0L; seconds < duration; seconds++) {
            requestCount += requestCountPerSecond
                .getOrDefault(epochTimeInSeconds - seconds, 0L);
        }
        System.out.printf("Request count in last %s seconds: %s%n", duration, requestCount);
        return requestCount;
    }

    // Compare and Set
    private void incrementCount(long epochTimeInSeconds) {
        if (requestCountPerSecond.putIfAbsent(epochTimeInSeconds, 1L) == null) {
            return;
        }
        Long old;
        do {
            old = requestCountPerSecond.get(epochTimeInSeconds);
        } while (!requestCountPerSecond.replace(epochTimeInSeconds, old, old + 1));
    }
}