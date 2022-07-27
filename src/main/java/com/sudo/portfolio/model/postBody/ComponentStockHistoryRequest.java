package com.sudo.portfolio.model.postBody;


import com.sudo.portfolio.model.yahoo.Interval;
import lombok.Data;

import java.util.List;

@Data
public class ComponentStockHistoryRequest {
    private List<String> symbols;
    private Interval interval;
}
