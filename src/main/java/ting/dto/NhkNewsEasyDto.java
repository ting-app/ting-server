package ting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

/**
 * The data transfer object that represents a nhk easy news.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NhkNewsEasyDto {
    private String title;

    private String titleWithRuby;

    private String outlineWithRuby;

    private String body;

    private String bodyWithoutRuby;

    private String url;

    private String m3u8Url;

    private String imageUrl;

    private Instant publishedAtUtc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleWithRuby() {
        return titleWithRuby;
    }

    public void setTitleWithRuby(String titleWithRuby) {
        this.titleWithRuby = titleWithRuby;
    }

    public String getOutlineWithRuby() {
        return outlineWithRuby;
    }

    public void setOutlineWithRuby(String outlineWithRuby) {
        this.outlineWithRuby = outlineWithRuby;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBodyWithoutRuby() {
        return bodyWithoutRuby;
    }

    public void setBodyWithoutRuby(String bodyWithoutRuby) {
        this.bodyWithoutRuby = bodyWithoutRuby;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getM3u8Url() {
        return m3u8Url;
    }

    public void setM3u8Url(String m3u8Url) {
        this.m3u8Url = m3u8Url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Instant getPublishedAtUtc() {
        return publishedAtUtc;
    }

    public void setPublishedAtUtc(Instant publishedAtUtc) {
        this.publishedAtUtc = publishedAtUtc;
    }
}
