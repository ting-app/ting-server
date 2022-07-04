package ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Course extends BaseEntity {
    @Column
    private String name;

    @Column
    private String description;

    @Column
    private int language;

    @Column
    private long createdBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }
}
