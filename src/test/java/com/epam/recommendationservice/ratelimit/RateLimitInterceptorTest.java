package com.epam.recommendationservice.ratelimit;

import com.epam.recommendationservice.config.ratelimit.RateLimitInterceptor;
import com.epam.recommendationservice.config.ratelimit.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.epam.recommendationservice.constants.RateLimitConstants.TOO_MANY_REQUESTS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class RateLimitInterceptorTest {

    public static final String API_KEY = "Key";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Bucket bucket;

    @Mock
    private ConsumptionProbe probe;

    @Mock
    private RateLimitService service;

    private RateLimitInterceptor rateLimitInterceptor;

    @Before
    public void init() {
        this.rateLimitInterceptor = new RateLimitInterceptor(service);
    }

    @Test
    public void preHandleTrueTest() throws Exception {
        when(request.getRemoteAddr()).thenReturn(API_KEY);
        when(service.resolveBucket(API_KEY)).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);

        assertTrue(this.rateLimitInterceptor.preHandle(request, response, mock(Object.class)));
    }

    @Test
    public void preHandleFalseTest() throws Exception {
        when(request.getRemoteAddr()).thenReturn(API_KEY);
        when(service.resolveBucket(API_KEY)).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(false);

        assertFalse(this.rateLimitInterceptor.preHandle(request, response, mock(Object.class)));
        verify(response, times(1)).sendError(HttpStatus.TOO_MANY_REQUESTS.value(), TOO_MANY_REQUESTS);
    }
}
