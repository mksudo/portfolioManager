package com.sudo.portfolio.service;

import com.sudo.portfolio.model.data.ComponentStock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class crawls wiki data to component stocks
 */
@Service
public class ComponentStockCrawlerService {

    /**
     * Crawl data from wikipedia
     * @return list of constructed component stocks
     */
    public List<ComponentStock> crawlComponentStocks() throws IOException {
        var result = new ArrayList<ComponentStock>();
        var wikiDoc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_S%26P_500_companies").get();
        var tableRows = wikiDoc.select("#constituents > tbody > tr");
        for (int rowIndex = 1; rowIndex < tableRows.size(); ++rowIndex) {
            Element tableRow = tableRows.get(rowIndex);
            result.add(
                    ComponentStock.builder()
                            .symbol(tableRow.child(0).text())
                            .security(tableRow.child(1).text())
                            .GICSSector(tableRow.child(3).text())
                            .GICSSubIndustry(tableRow.child(4).text())
                            .build()
            );
        }

        return result;
    }
}
