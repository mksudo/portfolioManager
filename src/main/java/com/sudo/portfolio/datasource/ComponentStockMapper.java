package com.sudo.portfolio.datasource;

import com.sudo.portfolio.model.data.ComponentStock;
import com.sudo.portfolio.model.data.State;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * This interface is a mybatis mapper to MySQL database
 */
@Mapper
public interface ComponentStockMapper {
    ComponentStock selectComponentStock(String symbol);

    List<ComponentStock> selectComponentStocks();

    int updateComponentStock(ComponentStock componentStock);

    int updateDataState(String symbol, State state);

    int insertComponentStock(ComponentStock componentStock);

    int insertComponentStocks(List<ComponentStock> componentStocks);

    int deleteComponentStock(String symbol);
}