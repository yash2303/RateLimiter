package com.yashasvi;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        APIRateLimiter rateLimiter = new APIRateLimiterSlidingWindow(60, 10L);
        try {
            for (int i = 0; i < 20; i++) {
                System.out.println(rateLimiter.isAccepted(new Date()) ? "Accepted" : "Rejected");
                System.out.println(rateLimiter.isAccepted(new Date()) ? "Accepted" : "Rejected");
                System.out.println(rateLimiter.isAccepted(new Date()) ? "Accepted" : "Rejected");
                System.out.println(rateLimiter.isAccepted(new Date()) ? "Accepted" : "Rejected");
                System.out.println("----------------------------------------------------------");
                Thread.sleep(10 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
