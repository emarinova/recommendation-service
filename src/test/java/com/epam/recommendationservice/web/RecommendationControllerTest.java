package com.epam.recommendationservice.web;

import com.epam.recommendationservice.service.CurrencyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.server.ResponseStatusException;

import static com.epam.recommendationservice.constants.RepositoryConstants.COULD_NOT_READ_CSV_FILE;
import static com.epam.recommendationservice.constants.ServiceConstants.CURRENCY_NOT_SUPPORTED;
import static com.epam.recommendationservice.constants.ServiceConstants.DATE_CANNOT_BE_PROCESSED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class RecommendationControllerTest {

    private static final String BLANK_VALUE = "";

    @Mock
    private CurrencyService service;

    private RecommendationController controller;

    @Before
    public void init() {
        this.controller = new RecommendationController(service);
    }

    @Test
    public void getNormalizedRangeValuesTest() {
        controller.getNormalizedRangeValues();
        verify(service, times(1)).getAllNormalizedRangeValuesSorted();
    }

    @Test
    public void getNormalizedRangeValuesCatchExceptionTest() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, COULD_NOT_READ_CSV_FILE);
        when(service.getAllNormalizedRangeValuesSorted()).thenThrow(exception);

        ResponseEntity responseEntity = controller.getNormalizedRangeValues();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(COULD_NOT_READ_CSV_FILE, responseEntity.getBody());
    }

    @Test
    public void getStatisticsForCurrencyTest() {
        controller.getStatisticsForCurrency(BLANK_VALUE);
        verify(service, times(1)).getCurrencyStatistics(BLANK_VALUE);
    }

    @Test
    public void getStatisticsForCurrencyCatchExceptionTest() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, CURRENCY_NOT_SUPPORTED);
        when(service.getCurrencyStatistics(BLANK_VALUE)).thenThrow(exception);

        ResponseEntity responseEntity = controller.getStatisticsForCurrency(BLANK_VALUE);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(CURRENCY_NOT_SUPPORTED, responseEntity.getBody());
    }

    @Test
    public void getHighestNormalizedRangeForDayTest() {
        controller.getHighestNormalizedRangeForDay(BLANK_VALUE);
        verify(service, times(1)).getHighestNormalizedRangeForDay(BLANK_VALUE);
    }

    @Test
    public void getHighestNormalizedRangeForDayCatchesExceptionTest() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, DATE_CANNOT_BE_PROCESSED);
        when(service.getHighestNormalizedRangeForDay(BLANK_VALUE)).thenThrow(exception);

        ResponseEntity responseEntity = controller.getHighestNormalizedRangeForDay(BLANK_VALUE);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(DATE_CANNOT_BE_PROCESSED, responseEntity.getBody());
    }
}
