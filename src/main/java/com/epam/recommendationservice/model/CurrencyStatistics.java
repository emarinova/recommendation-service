package com.epam.recommendationservice.model;

import java.util.Objects;

/**
 * CurrencyStatistics holds important currency records like
 * min price record for currency
 * max price record for currency
 * the oldest record for currency
 * the newest record for currency
 */
public class CurrencyStatistics {

    private final CurrencyRecord minPrice;
    private final CurrencyRecord maxPrice;
    private final CurrencyRecord oldestRecord;
    private final CurrencyRecord newestRecord;

    public CurrencyStatistics(CurrencyRecord minPrice, CurrencyRecord maxPrice, CurrencyRecord oldestRecord, CurrencyRecord newestRecord) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.oldestRecord = oldestRecord;
        this.newestRecord = newestRecord;
    }

    public CurrencyRecord getMinPrice() {
        return minPrice;
    }

    public CurrencyRecord getMaxPrice() {
        return maxPrice;
    }

    public CurrencyRecord getOldestRecord() {
        return oldestRecord;
    }

    public CurrencyRecord getNewestRecord() {
        return newestRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyStatistics that = (CurrencyStatistics) o;
        return Objects.equals(minPrice, that.minPrice) && Objects.equals(maxPrice, that.maxPrice) && Objects.equals(oldestRecord, that.oldestRecord) && Objects.equals(newestRecord, that.newestRecord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minPrice, maxPrice, oldestRecord, newestRecord);
    }
}
