package com.epam.recommendationservice.service.impl;

import com.epam.recommendationservice.model.CurrencyRecord;
import com.epam.recommendationservice.model.CurrencyStatistics;
import com.epam.recommendationservice.repository.CurrencyRepository;
import com.epam.recommendationservice.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.recommendationservice.constants.ServiceConstants.CURRENCY_NOT_SUPPORTED;
import static com.epam.recommendationservice.constants.ServiceConstants.DATE_CANNOT_BE_PROCESSED;
import static com.epam.recommendationservice.constants.ServiceConstants.NO_RECORDS_FOR_CURRENCY;
import static com.epam.recommendationservice.constants.ServiceConstants.NO_RECORDS_FOR_DATE;

/**
 * Currency Service holds the business logic
 * It gets its data from Crypto repository and transforms it in a way that it is required from the Controller
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    private final CurrencyRepository repository;

    @Autowired
    public CurrencyServiceImpl(CurrencyRepository repository) {
        this.repository = repository;
    }


    @Override
    public Map<String, Double> getAllNormalizedRangeValuesSorted() {
        Map<String, Double> normalizedRangeValues = new HashMap<>();

        Map<String, String> supportedCurrencies = this.repository.getAllSupportedCurrencies();
        for (Map.Entry<String, String> currency : supportedCurrencies.entrySet()) {
            String symbol = currency.getKey();
            String filePath = currency.getValue();
            List<CurrencyRecord> currencyRecords = this.repository.getCurrencyRecords(filePath);
            double normalizedRange = calculateNormalizedRange(currencyRecords);
            normalizedRangeValues.put(symbol, normalizedRange);
        }

        return normalizedRangeValues
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

    @Override
    public CurrencyStatistics getCurrencyStatistics(String currency) {
        Map<String, String> supportedCurrencies = this.repository.getAllSupportedCurrencies();
        if (!supportedCurrencies.containsKey(currency)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CURRENCY_NOT_SUPPORTED);
        }
        List<CurrencyRecord> currencyRecords = this.repository.getCurrencyRecords(supportedCurrencies.get(currency));
        if (currencyRecords.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_RECORDS_FOR_CURRENCY);
        }

        CurrencyRecord minPrice = CurrencyRecord.NOT_INITIALISED;
        CurrencyRecord maxPrice = CurrencyRecord.NOT_INITIALISED;
        CurrencyRecord oldestRecord = CurrencyRecord.NOT_INITIALISED;
        CurrencyRecord newestRecord = CurrencyRecord.NOT_INITIALISED;
        for (CurrencyRecord currencyRecord : currencyRecords) {
            Date timestamp = currencyRecord.getTimestamp();
            double price = currencyRecord.getPrice();
            if (minPrice.isNotInitialised() || minPrice.getPrice() > price) {
                minPrice = currencyRecord;
            }
            if (maxPrice.isNotInitialised() || maxPrice.getPrice() < price) {
                maxPrice = currencyRecord;
            }
            if (oldestRecord.isNotInitialised() || oldestRecord.getTimestamp().after(timestamp)) {
                oldestRecord = currencyRecord;
            }
            if (newestRecord.isNotInitialised() || newestRecord.getTimestamp().before(timestamp)) {
                newestRecord = currencyRecord;
            }
        }
        return new CurrencyStatistics(minPrice, maxPrice, oldestRecord, newestRecord);
    }

    @Override
    public Map.Entry<String, Double> getHighestNormalizedRangeForDay(String stringDate) {
        Date date;
        try {
            date = DATE_FORMATTER.parse(stringDate);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, DATE_CANNOT_BE_PROCESSED);
        }
        Map<String, Double> normalizedRangeValues = new HashMap<>();
        Map<String, String> supportedCurrencies = repository.getAllSupportedCurrencies();
        for (Map.Entry<String, String> currency : supportedCurrencies.entrySet()) {
            String symbol = currency.getKey();
            String filePath = currency.getValue();
            List<CurrencyRecord> currencyRecords = this.repository.getCurrencyRecords(filePath);
            List<CurrencyRecord> currencyRecordsForDate = getCurrencyRecordsForDate(currencyRecords, date);
            if (!currencyRecordsForDate.isEmpty()) {
                double normalizedRange = calculateNormalizedRange(currencyRecordsForDate);
                normalizedRangeValues.put(symbol, normalizedRange);
            }
        }
        if (normalizedRangeValues.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NO_RECORDS_FOR_DATE);
        }
        return normalizedRangeValues
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(1).toList()
                .get(0);

    }

    private List<CurrencyRecord> getCurrencyRecordsForDate(List<CurrencyRecord> currencyRecords, Date date) {
        List<CurrencyRecord> currencyRecordsForDate = new ArrayList<>();
        for (CurrencyRecord currencyRecord : currencyRecords) {
            if (isRecordForThisDate(date, currencyRecord)) {
                currencyRecordsForDate.add(currencyRecord);
            }
        }
        return currencyRecordsForDate;
    }

    private boolean isRecordForThisDate(Date date, CurrencyRecord currencyRecord) {
        Date timestamp = currencyRecord.getTimestamp();

        Calendar timestampCalendar = Calendar.getInstance();
        timestampCalendar.setTime(timestamp);
        timestampCalendar.set(Calendar.HOUR_OF_DAY, 0);
        timestampCalendar.set(Calendar.MINUTE, 0);
        timestampCalendar.set(Calendar.SECOND, 0);
        timestampCalendar.set(Calendar.MILLISECOND, 0);

        return timestampCalendar.getTime().equals(date);
    }

    private double calculateNormalizedRange(List<CurrencyRecord> currencyRecords) {
        double minPrice = Double.NaN;
        double maxPrice = Double.NaN;
        for (CurrencyRecord currencyRecord : currencyRecords) {
            double price = currencyRecord.getPrice();
            if (Double.isNaN(maxPrice) || minPrice > price) {
                minPrice = price;
            }
            if (Double.isNaN(maxPrice) || maxPrice < price) {
                maxPrice = price;
            }
        }
        return (maxPrice - minPrice) / minPrice;
    }
}
