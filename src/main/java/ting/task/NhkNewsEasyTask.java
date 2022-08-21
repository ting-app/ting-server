package ting.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ting.dto.NhkNewsEasyDto;
import ting.service.NhkNewsEasyService;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The daily task to fetch nhk easy news and saves them as tings.
 */
@Component
public class NhkNewsEasyTask {
    private Logger logger = LoggerFactory.getLogger(NhkNewsEasyTask.class);

    @Autowired
    private NhkNewsEasyService nhkNewsEasyService;

    /**
     * Fetch nhk easy news and saves them as tings.
     */
    @Scheduled(cron = "0 0 11 * * *", zone = "UTC")
    public void fetchNews() {
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        ZonedDateTime startDate = ZonedDateTime.of(
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                0, 0, 0, 1, ZoneId.of("UTC"));
        ZonedDateTime endDate = ZonedDateTime.of(
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                23, 59, 59, 1, ZoneId.of("UTC"));
        List<NhkNewsEasyDto> newsList = new ArrayList<>();

        try {
            newsList = nhkNewsEasyService.fetchNews(
                    startDate.toInstant(), endDate.toInstant());
        } catch (Exception e) {
            logger.error("Fetch nhk news error", e);
        }
    }
}
