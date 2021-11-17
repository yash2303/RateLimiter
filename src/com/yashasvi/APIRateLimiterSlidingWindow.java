package com.yashasvi;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class APIRateLimiterSlidingWindow implements APIRateLimiter {
    private final ConcurrentLinkedQueue<Long> queue;
    private final int duration;
    private final Long requestLimit;

    /**
     * @param duration     duration in seconds
     * @param requestLimit Throttle limit
     */
    public APIRateLimiterSlidingWindow(int duration, Long requestLimit) {
        this.duration = duration * 1000;
        this.requestLimit = requestLimit;
        this.queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean isAccepted(Date timestamp) {
        long epochTimeInSeconds = timestamp.getTime();
        System.out.printf("Request received at timestamp: %s%n", timestamp);
        while (!queue.isEmpty() && queue.peek() < epochTimeInSeconds - duration) {
            queue.poll();
        }
        System.out.printf("Request count in last %s seconds: %s%n", duration / 1000, queue.size());
        if (queue.size() >= requestLimit) {
            return false;
        }
        queue.add(epochTimeInSeconds);
        return true;
    }
}
