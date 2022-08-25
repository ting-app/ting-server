package ting.task;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ting.dto.NhkNewsEasyDto;
import ting.entity.Ting;
import ting.repository.TingRepository;
import ting.service.NhkNewsEasyService;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The daily task to fetch nhk easy news and saves them as tings.
 */
@Component
public class NhkNewsEasyTask {
    private Logger logger = LoggerFactory.getLogger(NhkNewsEasyTask.class);

    @Autowired
    private NhkNewsEasyService nhkNewsEasyService;

    @Autowired
    private TingRepository tingRepository;

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

        if (CollectionUtils.isEmpty(newsList)) {
            return;
        }

        List<Ting> tingList = newsList.stream()
                .map(news -> {
                    Ting ting = new Ting();
                    ting.setProgramId(1L);
                    ting.setTitle(news.getTitle());
                    ting.setDescription(extractText(trimRuby(news.getOutlineWithRuby())));
                    ting.setAudioUrl(news.getM3u8Url());
                    ting.setContent(extractText(news.getBodyWithoutRuby()));
                    ting.setCreatedAt(now.toInstant());
                    ting.setUpdatedAt(now.toInstant());

                    return ting;
                })
                .collect(Collectors.toList());

        tingRepository.saveAll(tingList);
    }

    private String extractText(String html) {
        Document document = Jsoup.parse(html);

        return document.text();
    }

    private String trimRuby(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ruby");

        elements.forEach(element -> element.select("rt").remove());

        return document.select("body").html();
    }
}
