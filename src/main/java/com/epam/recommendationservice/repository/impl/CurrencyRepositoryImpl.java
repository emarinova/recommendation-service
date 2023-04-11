package com.epam.recommendationservice.repository.impl;

import com.epam.recommendationservice.model.CurrencyRecord;
import com.epam.recommendationservice.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.recommendationservice.constants.RepositoryConstants.COULD_NOT_READ_CSV_FILE;
import static com.epam.recommendationservice.constants.RepositoryConstants.REGEX;

/**
 * Currency Repository is used to get all files holding crypto currency records along with supported currencies
 * It also reads csv files and transforms them in Collections of CurrencyRecords
 */
@Repository
@ConfigurationProperties
public class CurrencyRepositoryImpl implements CurrencyRepository {

    private final String directoryPath;

    public CurrencyRepositoryImpl(@Value("${dir.path}") final String directoryPath) {
        this.directoryPath = directoryPath;
    }

    /**
     * Gets all files in predefined directory and creates a map with currency as key and file path as value
     * @return Map of currency - file path with records
     */
    @Override
    public Map<String, String> getAllSupportedCurrencies() {
        Map<String, String> allSupportedCurrencies = new HashMap<>();
        File[] allFiles = getAllFiles();
        for (File file : allFiles) {
            String filePath = this.directoryPath + file.getName();
            String currency = file.getName().split(REGEX)[0];
            allSupportedCurrencies.put(currency, filePath);
        }
        return allSupportedCurrencies;
    }

    /**
     * Reads file and maps its rows to CurrencyRecord objects
     * @param filePath
     * @return List of CurrencyRecord objects
     */
    @Override
    public List<CurrencyRecord> getCurrencyRecords(String filePath) {
        try {
            CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
            CsvMapper mapper = new CsvMapper();
            File file = new File(filePath);
            MappingIterator<CurrencyRecord> readValues =
                    mapper.readerFor(CurrencyRecord.class).with(bootstrapSchema).readValues(file);
            return readValues.readAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, COULD_NOT_READ_CSV_FILE + filePath);
        }
    }

    private File[] getAllFiles() {
        File directoryPath = new File(this.directoryPath);
        return directoryPath.listFiles();
    }
}
