package com.epam.recommendationservice.repository;

import com.epam.recommendationservice.model.CurrencyRecord;

import java.util.List;
import java.util.Map;

public interface CurrencyRepository {

   Map<String, String> getAllSupportedCurrencies();
   List<CurrencyRecord> getCurrencyRecords(String filePath);
}
