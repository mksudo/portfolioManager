package com.sudo.portfolio.service;


import com.sudo.portfolio.model.portfolio.AnalyzedResults;
import com.sudo.portfolio.model.portfolio.TimedResult;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.springframework.stereotype.Service;

import java.util.Comparator;


/**
 * This class calculates risk related
 * data from portfolio results
 */
@Service
public class PortfolioRiskService {

    /**
     * Calculate the standard deviation of returns
     * @param analyzedResults the analyzed results of data
     * @return the standard deviation of the data
     */
    public double calculateStandardDeviation(
            AnalyzedResults<Double> analyzedResults
    ) {
        StandardDeviation standardDeviation = new StandardDeviation();
        var returns = analyzedResults.getResults();
        return standardDeviation
                .evaluate(
                        returns
                                .stream()
                                .mapToDouble(TimedResult::getData)
                                .toArray()
                );
    }

    /**
     * Calculate the variance of returns based in input percentage
     * @param analyzedResults the analyzed results of data
     * @param variancePercent the percentage of variance
     * @return the percent variance of the data
     */
    public TimedResult<Double> calculateVariance(
            AnalyzedResults<Double> analyzedResults,
            double variancePercent
    ) {
        var returns = analyzedResults.getResults();
        returns.sort(Comparator.comparing(TimedResult::getData));
        int pointIndex = (int) Math.floor((1 - variancePercent) * returns.size());
        return returns.get(pointIndex);
    }
}
