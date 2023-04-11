package com.epam.recommendationservice.config.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final int capacity;

    private final int minutes;

    private final Map<String, Bucket> cache;

    public RateLimitService(@Value("${rate.limit.capacity}") final int capacity,
                            @Value("${rate.limit.duration.minutes}") final int minutes) {
        this.capacity = capacity;
        this.minutes = minutes;
        this.cache = new ConcurrentHashMap<>();
    }

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.intervally(capacity, Duration.ofMinutes(minutes))))
                .build();
    }
}
