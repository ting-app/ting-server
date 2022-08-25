package ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;

/**
 * The program entity.
 */
@Entity
public class Program extends BaseEntity {
    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Integer language;

    @Column
    private Boolean visible;

    @Column
    private Long createdBy;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
