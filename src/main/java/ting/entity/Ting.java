package ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;

@Entity
public class Ting extends BaseEntity {
    @Column
    private long programId;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String audioUrl;

    @Column
    private String content;

    @Column
    private Instant createdAt;

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }

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

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
