package ting.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

public class TingPracticeDto {
    private long id;

    private long userId;

    private long tingId;

    @NotBlank
    private String content;

    @Min(0)
    private float score;

    @Min(0)
    private long timeCostInSeconds;

    private Instant createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTingId() {
        return tingId;
    }

    public void setTingId(long tingId) {
        this.tingId = tingId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getTimeCostInSeconds() {
        return timeCostInSeconds;
    }

    public void setTimeCostInSeconds(long timeCostInSeconds) {
        this.timeCostInSeconds = timeCostInSeconds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
