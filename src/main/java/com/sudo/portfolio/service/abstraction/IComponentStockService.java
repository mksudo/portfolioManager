package com.sudo.portfolio.service.abstraction;

import com.sudo.portfolio.model.data.ComponentStock;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface IComponentStockService {
    ComponentStock selectComponentStock(String symbol);

    List<ComponentStock> selectComponentStocks();

    int updateComponentStock(@NotNull ComponentStock componentStock);

    int insertComponentStock(@NotNull ComponentStock componentStock);

    int insertComponentStocks(List<ComponentStock> componentStocks);

    int deleteComponentStock(String symbol);
}
