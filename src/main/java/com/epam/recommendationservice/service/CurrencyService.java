package com.epam.recommendationservice.service;

import com.epam.recommendationservice.model.CurrencyStatistics;

import java.util.Map;

public interface CurrencyService {
    Map<String, Double> getAllNormalizedRangeValuesSorted();

    CurrencyStatistics getCurrencyStatistics(String currency);

    Map.Entry<String, Double> getHighestNormalizedRangeForDay(String stringDate);
}
