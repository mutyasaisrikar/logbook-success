package com.example.logbookerror.logbook;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

@Slf4j
@Component
public class CustomHttpLogWriter implements HttpLogWriter {

    private static final Collector<CharSequence, ?, String> JOINER = Collectors.joining(" ");

    @Override
    public void writeRequest(Precorrelation<String> precorrelation) {
        //nothing to do
    }


    @Override
    public void writeResponse(Correlation<String, String> correlation) throws IOException {

        logExchange(correlation);
    }

    private void logExchange(Correlation<String, String> correlation) throws IOException {
        Map<String, Object> exchangeItems = new LinkedHashMap<>();
        exchangeItems.put("endpoint", correlation.getOriginalRequest().getPath());
        exchangeItems.put("method", correlation.getOriginalRequest().getMethod());
        exchangeItems.put("responseTime", correlation.getDuration().toMillis());
        exchangeItems.put("headers", correlation.getOriginalRequest().getHeaders());
        exchangeItems.put("queryParameters", correlation.getOriginalRequest().getQuery());
        exchangeItems.put("requestBody", correlation.getOriginalRequest().getBodyAsString());
        exchangeItems.put("responseStatus", correlation.getOriginalResponse().getStatus());
        exchangeItems.put("responseHeaders", correlation.getOriginalResponse().getHeaders());
        exchangeItems.put("responseBody", correlation.getOriginalResponse().getBodyAsString());

        log.info(getLogText(exchangeItems));
    }


    private static String getLogText(Map<String, Object> data) {
        return data.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + trimToEmpty(entry.getValue()))
            .collect(JOINER);
    }

    private static String trimToEmpty(Object object) {
        return object == null ? "" : object.toString().trim();
    }
}
