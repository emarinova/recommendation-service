package com.epam.recommendationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.Objects;

/**
 * CurrencyRecord object store single line of csv file
 * CurrencyRecord holds currency and its price for a concrete timestamp
 */
public class CurrencyRecord {
    public static final CurrencyRecord NOT_INITIALISED = new CurrencyRecord();
    private Date timestamp;
    private String symbol;
    private double price;

    private CurrencyRecord() {
    }

    public CurrencyRecord(Date timestamp, String symbol, double price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    @JsonIgnore
    public boolean isNotInitialised() {
        return this.equals(NOT_INITIALISED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyRecord that = (CurrencyRecord) o;
        return Double.compare(that.price, price) == 0 && Objects.equals(timestamp, that.timestamp) && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, symbol, price);
    }
}
