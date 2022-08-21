package ting.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ting.config.TingConfig;
import ting.dto.NhkNewsEasyDto;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The service to fetch nhk easy news.
 */
@Service
public class NhkNewsEasyService {
    // When milliseconds is zero, Instant.toString() produces something like 2018-05-25T18:56:09Z,
    // but we want 2018-05-25T18:56:09.000Z, thus we need a custom formatter
    private static final DateTimeFormatter isoDateTimeFormatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendInstant(3)
            .toFormatter();

    @Autowired
    private TingConfig tingConfig;

    /**
     * Fetch news with given date range.
     *
     * @param startDate The start date
     * @param endDate   The end date
     * @return List of {@link ting.dto.NhkNewsEasyDto}
     * @throws IOException Network error
     */
    public List<NhkNewsEasyDto> fetchNews(Instant startDate, Instant endDate) throws IOException {
        Objects.requireNonNull(startDate, "startDate cannot be null");
        Objects.requireNonNull(endDate, "endDate cannot be null");

        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.parse(tingConfig.getNhkApiHost() + "/news")
                .newBuilder()
                .addQueryParameter("startDate", isoDateTimeFormatter.format(startDate))
                .addQueryParameter("endDate", isoDateTimeFormatter.format(endDate))
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();
        Response response = okHttpClient.newCall(request).execute();

        if (response.code() != 200) {
            return Collections.emptyList();
        }

        String json = response.body().string();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(javaTimeModule);

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }
}
