package com.sudo.portfolio.servicer;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sudo.portfolio.model.yahoo.ComponentStockHistory;
import com.sudo.portfolio.model.yahoo.Interval;
import com.sudo.portfolio.model.yahoo.Range;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;


/**
 * This class crawls history data from yahoo api
 * and deserialize them into component stock histories
 */
@Service
public class YahooCrawlerService {

    private static final String baseUrl = "https://query1.finance.yahoo.com/v8/finance/chart/";

    /**
     * Deserializes json content returned by the yahoo finance api
     * and constructs corresponding list of histories
     * @param gson the gson object to perform deserialization on
     * @param jsonContent the content returned by the yahoo finance api
     * @param symbol the symbol of the component stock
     * @return list of deserialized histories
     */
    private List<ComponentStockHistory> deserializeStockQuoteHistory(
            Gson gson,
            String jsonContent,
            String symbol
    ) {
        JsonObject resultObj = gson.fromJson(jsonContent, JsonElement.class).getAsJsonObject();

        JsonObject chart = resultObj.getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject();
        // length of timestamp is the same as all the following arrays
        JsonArray timestampArray = chart.getAsJsonArray("timestamp");

        JsonObject quoteResult = chart.getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject();

        JsonArray openArray = quoteResult.getAsJsonArray("open");
        JsonArray closeArray = quoteResult.getAsJsonArray("close");
        JsonArray highArray = quoteResult.getAsJsonArray("high");
        JsonArray lowArray = quoteResult.getAsJsonArray("low");
        JsonArray volumeArray = quoteResult.getAsJsonArray("volume");

        List<ComponentStockHistory> stockQuoteHistories = new ArrayList<>();

        for (int timestampIndex = 0; timestampIndex < timestampArray.size(); ++timestampIndex) {
            JsonElement timestampElement = timestampArray.get(timestampIndex);
            // use null as null object value and -1 as null primitive value for histories
            Instant timestamp = timestampElement.isJsonNull() ?
                    null :
                    Date.from(
                            Instant.ofEpochSecond(
                                    timestampElement
                                            .getAsJsonPrimitive()
                                            .getAsLong()
                            )
                    ).toInstant();

            JsonElement openPriceElement = openArray.get(timestampIndex);
            double openPrice = openPriceElement.isJsonNull() ?
                    -1 : openPriceElement
                    .getAsJsonPrimitive()
                    .getAsDouble();

            JsonElement closePriceElement = closeArray.get(timestampIndex);
            double closePrice = closePriceElement.isJsonNull() ?
                    -1 : closePriceElement
                    .getAsJsonPrimitive()
                    .getAsDouble();

            JsonElement highPriceElement = highArray.get(timestampIndex);
            double highPrice = highPriceElement.isJsonNull() ?
                    -1 : highPriceElement
                    .getAsJsonPrimitive()
                    .getAsDouble();

            JsonElement lowPriceElement = lowArray.get(timestampIndex);
            double lowPrice = lowPriceElement.isJsonNull() ?
                    -1 : lowPriceElement
                    .getAsJsonPrimitive()
                    .getAsDouble();

            JsonElement volumeElement = volumeArray.get(timestampIndex);
            int volume = volumeElement.isJsonNull() ?
                    -1 : volumeElement
                    .getAsJsonPrimitive()
                    .getAsInt();

            stockQuoteHistories.add(
                    ComponentStockHistory.builder()
                            .symbol(symbol)
                            .time(timestamp)
                            .open(openPrice)
                            .close(closePrice)
                            .high(highPrice)
                            .low(lowPrice)
                            .volume(volume)
                            .build()
            );
        }

        return stockQuoteHistories;
    }

    /**
     * Format the input symbol to request url with from, to, and interval specified
     * @param symbol symbol of the component stock
     * @param from from timestamp
     * @param to to timestamp
     * @param interval interval of data
     * @return an entry with symbol as key and request url as value
     */
    private Map.Entry<String, String> getRequestUrl(
            String symbol,
            Instant from,
            Instant to,
            Interval interval
    ) {
        return new AbstractMap.SimpleEntry<>(
                symbol,
                String.format(
                        baseUrl + "%s?period1=%d&period2=%d&interval=%s",
                        symbol,
                        from.getEpochSecond(),
                        to.getEpochSecond(),
                        interval.getInterval()
                )
        );
    }

    /**
     * Format the input symbol to request url with range and interval specified
     * @param symbol symbol of the component stock
     * @param range range of requested data
     * @param interval interval of data
     * @return an entry with symbol as key and request url as value
     */
    private Map.Entry<String, String> getRequestUrl(
            String symbol,
            Range range,
            Interval interval
    ) {
        return new AbstractMap.SimpleEntry<>(
                symbol,
                String.format(baseUrl + "%s?range=%s&interval=%s",
                        symbol,
                        range.getRange(),
                        interval.getInterval()
                )
        );
    }

    /**
     * Format symbols to corresponding urls and return
     * @param symbols symbols of requested component stocks
     * @param from from timestamp
     * @param to to timestamp
     * @param interval interval of data
     * @return list of entries with symbol as key and request url as value
     */
    private List<Map.Entry<String, String>> getRequestUrls(
            List<String> symbols,
            Instant from,
            Instant to,
            Interval interval
    ) {
        return symbols
                .stream()
                .map(
                        symbol -> getRequestUrl(symbol, from, to, interval)
                ).toList();
    }

    /**
     * Format symbols to corresponding urls and return
     * @param symbols symbols of requested component stocks
     * @param range range of requested data
     * @param interval interval of data
     * @return list of entries with symbol as key and request url as value
     */
    private List<Map.Entry<String, String>> getRequestUrls(
            List<String> symbols,
            Range range,
            Interval interval
    ) {
        return symbols
                .stream()
                .map(
                        symbol -> getRequestUrl(symbol, range, interval)
                ).toList();
    }

    /**
     * Construct completable future of deserialized results for a request url
     * @param pair entry with symbol as key and request url as value
     * @param client http client to send the request
     * @param gson gson object to perform deserialization
     * @return completable futures of request and deserialization process
     */
    private CompletableFuture<List<ComponentStockHistory>> requestComponentStockHistory(
            Map.Entry<String, String> pair,
            HttpClient client,
            Gson gson
    ) {
        try {
            return client
                    .sendAsync(
                            HttpRequest.newBuilder(
                                    new URI(pair.getValue())
                            ).GET().build(),
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    ).thenApply(
                            stringHttpResponse -> deserializeStockQuoteHistory(
                                    gson,
                                    stringHttpResponse.body(),
                                    pair.getKey()
                            )
                    );
        } catch (URISyntaxException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * Construct completable future of deserialized results for a list of request urls
     * @param pairs entries with symbol as key and request url as value
     * @param client http client to send the request
     * @param gson gson object to perform deserialization
     * @return completable futures of request and deserialization processes
     */
    private List<CompletableFuture<List<ComponentStockHistory>>> requestComponentStockHistories(
            List<Map.Entry<String, String>> pairs,
            HttpClient client,
            Gson gson
    ) {
        return pairs
                .stream()
                .map(
                        pair -> requestComponentStockHistory(pair, client, gson)
                )
                .toList();
    }

    /**
     * Wait for all completable futures to finish and join
     * the deserialized lists of component histories
     * @param requestFutures completable futures of request and deserialization processes
     * @return merged list of component stock histories
     */
    private List<ComponentStockHistory> waitForRequestFutureAndMerge(
            List<CompletableFuture<List<ComponentStockHistory>>> requestFutures
    ) {
        return requestFutures
                .stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Get component stock histories from yahoo finance api by from, to, and interval
     * @param symbols symbols of requested component stocks
     * @param from from timestamp
     * @param to to timestamp
     * @param interval interval of data
     * @return list of requested component stock histories
     */
    public List<ComponentStockHistory> getStockHistories(List<String> symbols, Instant from, Instant to, Interval interval) {
        Gson gson = new Gson();
        HttpClient client = HttpClient.newBuilder().executor(Executors.newFixedThreadPool(8)).build();

        return waitForRequestFutureAndMerge(
                requestComponentStockHistories(
                        getRequestUrls(symbols, from, to, interval),
                        client,
                        gson
                )
        );
    }

    /**
     * Get component stock histories from yahoo finance api by range and interval
     * @param symbols symbols of requested component stocks
     * @param range range of requested data
     * @param interval interval of data
     * @return list of requested component stock histories
     */
    public List<ComponentStockHistory> getStockHistories(List<String> symbols, Range range, Interval interval) {
        Gson gson = new Gson();
        HttpClient client = HttpClient.newBuilder().executor(Executors.newFixedThreadPool(8)).build();

        return waitForRequestFutureAndMerge(
                requestComponentStockHistories(
                        getRequestUrls(symbols, range, interval),
                        client,
                        gson
                )
        );
    }
}
