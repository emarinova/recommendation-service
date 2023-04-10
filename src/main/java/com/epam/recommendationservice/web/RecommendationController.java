package com.epam.recommendationservice.web;

import com.epam.recommendationservice.model.CurrencyStatistics;
import com.epam.recommendationservice.service.CurrencyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;

@RestController
public class RecommendationController {

    private final CurrencyService service;

    @Autowired
    public RecommendationController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping(value = URLs.V1_NORMALIZED_RANGE)
    @ApiOperation(value = "Shows descending sorted list of the normalized range values for all supported crypto currencies")
    public ResponseEntity getNormalizedRangeValues() {
        try {
            Map<String, Double> allNormalizedRangeValuesSorted = service.getAllNormalizedRangeValuesSorted();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(allNormalizedRangeValuesSorted);
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatus())
                    .body(e.getReason());
        }
    }

    @GetMapping(value = URLs.V1_STATISTICS + "/{currency}")
    @ApiOperation(value = "Shows min/max/oldest/newest values for currency")
    public ResponseEntity getStatisticsForCurrency(@ApiParam(value = "currency", required = true)
                                                   @PathVariable String currency) {
        try {
            CurrencyStatistics currencyStatistics = service.getCurrencyStatistics(currency.toUpperCase(Locale.ROOT));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(currencyStatistics);
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatus())
                    .body(e.getReason());
        }
    }

    @GetMapping(value = URLs.V1_NORMALIZED_RANGE + "/{date}")
    @ApiOperation(value = "Shows currency with highest normalized range for specific day")
    public ResponseEntity getHighestNormalizedRangeForDay(@ApiParam(value = "date in format dd-MM-yyyy", required = true)
                                                          @PathVariable String date) {
        try {
            Map.Entry<String, Double> highestNormalizedRangeForDay = service.getHighestNormalizedRangeForDay(date);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(highestNormalizedRangeForDay);
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatus())
                    .body(e.getReason());
        }
    }
}
