package ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;

@Entity
public class TingPractice extends BaseEntity {
    @Column
    private long userId;

    @Column
    private long tingId;

    @Column(columnDefinition = "text")
    private String content;

    @Column
    private float score;

    @Column
    private Instant createdAt;

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
