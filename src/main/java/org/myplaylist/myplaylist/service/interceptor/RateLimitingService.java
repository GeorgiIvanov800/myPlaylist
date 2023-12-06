package org.myplaylist.myplaylist.service.interceptor;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitingService {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    public boolean isAllowed(String clientIp) {
        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));
        int requests = requestCounts.get(clientIp).incrementAndGet();

        // Reset counter every minute
        requestCounts.get(clientIp).compareAndSet(MAX_REQUESTS_PER_MINUTE, 0);

        return requests <= MAX_REQUESTS_PER_MINUTE;
    }
}
