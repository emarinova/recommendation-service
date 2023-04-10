package com.epam.recommendationservice.service;

import com.epam.recommendationservice.model.CurrencyRecord;
import com.epam.recommendationservice.model.CurrencyStatistics;
import com.epam.recommendationservice.repository.CurrencyRepository;
import com.epam.recommendationservice.service.impl.CurrencyServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

import static com.epam.recommendationservice.constants.ServiceConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class CurrencyServiceImplTest {

    private static final String BTC = "BTC";
    private static final String ETH = "ETH";
    private static final double ZERO_NORMALIZED = 0.0;
    private static final double ONE_NORMALIZED = 1.0;
    private static final CurrencyRecord ETH_RECORD_1 = new CurrencyRecord(Date.from(Instant.ofEpochMilli(1642122000000L)), ETH, 10);
    private static final CurrencyRecord ETH_RECORD_2 = new CurrencyRecord(Date.from(Instant.ofEpochMilli(1641711600000L)), ETH, 20);

    @Mock
    private CurrencyRepository repository;

    private CurrencyService service;

    @Before
    public void setup() {
        this.service = new CurrencyServiceImpl(repository);

        when(repository.getAllSupportedCurrencies()).thenReturn(new HashMap<>() {{
            put(BTC, BTC);
            put(ETH, ETH);
        }});

        when(repository.getCurrencyRecords(ETH))
                .thenReturn(new ArrayList<>() {{
                    add(ETH_RECORD_1);
                    add(ETH_RECORD_2);
                }});
    }

    @Test
    public void getAllNormalizedRangeValuesSortedTest() {
        when(repository.getCurrencyRecords(BTC))
                .thenReturn(Collections.singletonList(new CurrencyRecord(Date.from(Instant.now()), BTC, 20)));

        Map<String, Double> expected = new HashMap<>(){{
            put(ETH, ONE_NORMALIZED);
            put(BTC, ZERO_NORMALIZED);
        }};
        Map<String, Double> actual = this.service.getAllNormalizedRangeValuesSorted();

        assertEquals(expected, actual);
    }

    @Test
    public void getCurrencyStatisticsTest() {
        CurrencyStatistics expected = new CurrencyStatistics(ETH_RECORD_1, ETH_RECORD_2, ETH_RECORD_2, ETH_RECORD_1);
        CurrencyStatistics actual = this.service.getCurrencyStatistics(ETH);

        assertEquals(expected, actual);
    }

    @Test
    public void getCurrencyStatisticsThrowsNoSupportedCurrencyTest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> this.service.getCurrencyStatistics(""));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(CURRENCY_NOT_SUPPORTED, exception.getReason());
    }

    @Test
    public void getCurrencyStatisticsThrowsNoRecordsTest() {
        when(repository.getCurrencyRecords(BTC))
                .thenReturn(new ArrayList<>());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> this.service.getCurrencyStatistics(BTC));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(NO_RECORDS_FOR_CURRENCY, exception.getReason());
    }

    @Test
    public void getHighestNormalizedRangeForDayTest() {
        Map.Entry<String, Double> expected = Map.entry(ETH, ZERO_NORMALIZED);
        Map.Entry<String, Double> actual = this.service.getHighestNormalizedRangeForDay("14-01-2022");
        assertEquals(expected, actual);
    }

    @Test
    public void getHighestNormalizedRangeForDayParseExceptionTest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> this.service.getHighestNormalizedRangeForDay(""));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(DATE_CANNOT_BE_PROCESSED, exception.getReason());
    }

    @Test
    public void getHighestNormalizedRangeForDayNoRecordsTest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> this.service.getHighestNormalizedRangeForDay("14-01-2023"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(NO_RECORDS_FOR_DATE, exception.getReason());
    }

}
