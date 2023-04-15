package dekiru.ting.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * The data transfer object that represents a practice record of a ting.
 */
public class TingPracticeDto {
    private Long id;

    private Long createdBy;

    @NotNull(message = "听力 id 不能为空")
    private Long tingId;

    private String tingTitle;

    @NotBlank(message = "听力内容不能为空")
    private String content;

    @NotNull(message = "分数不能为空")
    @Min(value = 0, message = "分数的最小值为0")
    private Float score;

    @NotNull(message = "耗时不能为空")
    @Min(value = 0, message = "耗时的最小值为0")
    private Long timeCostInSeconds;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getTingId() {
        return tingId;
    }

    public void setTingId(Long tingId) {
        this.tingId = tingId;
    }

    public String getTingTitle() {
        return tingTitle;
    }

    public void setTingTitle(String tingTitle) {
        this.tingTitle = tingTitle;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
