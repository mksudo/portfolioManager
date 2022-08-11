package com.sudo.portfolio.model.portfolio;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
public class AnalyzedResults<T> {
    Map<String, List<TimedResult<T>>> allocatedResults;
    List<TimedResult<T>> results;

    public AnalyzedResults(int historyCount) {
        this.allocatedResults = new HashMap<>();
        this.results = new ArrayList<>(historyCount);
    }
}
