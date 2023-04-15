package dekiru.ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;

/**
 * The ting practice entity.
 */
@Entity
public class TingPractice extends BaseEntity {
    @Column
    private Long tingId;

    @Column(columnDefinition = "text")
    private String content;

    @Column
    private Float score;

    @Column
    private Long timeCostInSeconds;

    @Column
    private Long createdBy;

    @Column
    private Instant createdAt;

    public Long getTingId() {
        return tingId;
    }

    public void setTingId(Long tingId) {
        this.tingId = tingId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Long getTimeCostInSeconds() {
        return timeCostInSeconds;
    }

    public void setTimeCostInSeconds(Long timeCostInSeconds) {
        this.timeCostInSeconds = timeCostInSeconds;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
