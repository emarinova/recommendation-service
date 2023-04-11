package com.epam.recommendationservice.ratelimit;

import com.epam.recommendationservice.config.ratelimit.RateLimitService;
import io.github.bucket4j.Bucket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class RateLimitServiceTest {

    private static final String API_KEY = "key";
    private static final String API_KEY_2 = "key2";
    private static final int CAPACITY = 1;
    private RateLimitService rateLimitService;

    @Before
    public void init() {
        this.rateLimitService = new RateLimitService(CAPACITY, 1);
    }

    @Test
    public void resolveBucketTest() {
        Bucket firstBucket = this.rateLimitService.resolveBucket(API_KEY);
        assertTrue(firstBucket.tryConsume(CAPACITY));

        Bucket secondBucket = this.rateLimitService.resolveBucket(API_KEY);
        assertFalse(secondBucket.tryConsume(CAPACITY));
    }

    @Test
    public void resolveBucketDifferentKeyTest() {
        Bucket firstBucket = this.rateLimitService.resolveBucket(API_KEY);
        assertTrue(firstBucket.tryConsume(CAPACITY));

        Bucket secondBucket = this.rateLimitService.resolveBucket(API_KEY_2);
        assertTrue(secondBucket.tryConsume(CAPACITY));
    }
}
