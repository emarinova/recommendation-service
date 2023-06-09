package com.epam.recommendationservice.repository;

import com.epam.recommendationservice.model.CurrencyRecord;
import com.epam.recommendationservice.repository.impl.CurrencyRepositoryImpl;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.recommendationservice.constants.RepositoryConstants.COULD_NOT_READ_CSV_FILE;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class CurrencyRepositoryImplTest {

    private static final String FILE_PATH = "src/main/resources/prices/BTC_values.csv";

    private CurrencyRepository repository;

    @Before
    public void setup() {
        this.repository = new CurrencyRepositoryImpl("src/main/resources/prices/");
    }

    @Test
    public void getAllSupportedCurrenciesTest() {
        Map<String, String> expected = new HashMap<>() {{
            put("BTC", "src/main/resources/prices/BTC_values.csv");
            put("DOGE", "src/main/resources/prices/DOGE_values.csv");
            put("ETH", "src/main/resources/prices/ETH_values.csv");
            put("LTC", "src/main/resources/prices/LTC_values.csv");
            put("XRP", "src/main/resources/prices/XRP_values.csv");
        }};
        Map<String, String> actual = this.repository.getAllSupportedCurrencies();
        assertEquals(expected, actual);
    }

    @Test
    public void getCurrencyRecordsTest() throws IOException {
        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();
        File file = new File(FILE_PATH);
        MappingIterator<CurrencyRecord> iterator = mapper
                .readerFor(CurrencyRecord.class)
                .with(bootstrapSchema)
                .readValues(file);
        List<CurrencyRecord> expected = iterator.readAll();

        List<CurrencyRecord> actual = this.repository.getCurrencyRecords(FILE_PATH);

        assertEquals(expected, actual);
    }

    @Test
    public void getCurrencyRecordsThrowsExceptionTest() {
        ResponseStatusException exception = Assert.assertThrows(ResponseStatusException.class, () -> repository.getCurrencyRecords(""));
        assertEquals(COULD_NOT_READ_CSV_FILE, exception.getReason());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }
}
