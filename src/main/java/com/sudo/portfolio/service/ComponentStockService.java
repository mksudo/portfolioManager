package com.sudo.portfolio.service;

import com.sudo.portfolio.datasource.ComponentStockMapper;
import com.sudo.portfolio.model.data.ComponentStock;
import com.sudo.portfolio.model.data.State;
import com.sudo.portfolio.service.abstraction.IComponentStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * This class handles communications with MySQL
 */
@Service
public class ComponentStockService implements IComponentStockService {

    private final ComponentStockMapper componentStockMapper;

    @Autowired
    public ComponentStockService(
            ComponentStockMapper componentStockMapper
    ) {
        this.componentStockMapper = componentStockMapper;
    }

    /**
     * Select component stock by symbol
     * @param symbol symbol of component stock
     * @return component stock
     */
    public ComponentStock selectComponentStock(String symbol) {
        return this.componentStockMapper.selectComponentStock(symbol);
    }

    /**
     * Select all component stocks in the database
     * @return list of component stocks
     */
    public List<ComponentStock> selectComponentStocks() {
        return this.componentStockMapper.selectComponentStocks();
    }

    /**
     * Update a component stock by its values
     * @param componentStock the updated component stock
     * @return how many rows are affected
     */
    public int updateComponentStock(@NotNull ComponentStock componentStock) {
        return this.componentStockMapper.updateComponentStock(componentStock);
    }

    /**
     * Insert a new component stock to the database
     * @param componentStock the component stock to be inserted
     * @return how many rows are affected
     */
    public int insertComponentStock(@NotNull ComponentStock componentStock) {
        this.componentStockMapper.updateDataState(componentStock.getSymbol(), State.OUTDATED);

        return this.componentStockMapper.insertComponentStock(componentStock);
    }

    /**
     * Insert a list of component stocks to the database
     * @param componentStocks the component stocks to be inserted
     * @return how many rows are affected
     */
    public int insertComponentStocks(List<ComponentStock> componentStocks) {
        return this.componentStockMapper.insertComponentStocks(componentStocks);
    }

    /**
     * Delete component stock records in the database by marking them as DELETED
     * @param symbol symbol of the component stock records
     * @return how many rows are affected
     */
    public int deleteComponentStock(String symbol) {
        return this.componentStockMapper.deleteComponentStock(symbol);
    }
}
