package ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;

/**
 * The ting practice entity.
 */
@Entity
public class TingPractice extends BaseEntity {
    @Column
    private long createdBy;

    @Column
    private long tingId;

    @Column(columnDefinition = "text")
    private String content;

    @Column
    private float score;

    @Column
    private long timeCostInSeconds;

    @Column
    private Instant createdAt;

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
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
