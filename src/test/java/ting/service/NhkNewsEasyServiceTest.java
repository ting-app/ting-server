package ting.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ting.BaseTest;
import ting.dto.NhkNewsEasyDto;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class NhkNewsEasyServiceTest extends BaseTest {
    @Autowired
    private NhkNewsEasyService nhkNewsEasyService;

    @Test
    public void shouldFetchNews() throws IOException {
        ZonedDateTime startDate = ZonedDateTime.of(
                2022, 1, 1,
                0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime endDate = ZonedDateTime.of(
                2022, 1, 1,
                23, 59, 59, 0, ZoneId.of("UTC"));
        List<NhkNewsEasyDto> newsList = nhkNewsEasyService.fetchNews(
                startDate.toInstant(), endDate.toInstant());

        Assertions.assertFalse(newsList.isEmpty());
    }
}
