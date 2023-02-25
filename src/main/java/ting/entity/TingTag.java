package ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * The ting tag entity.
 */
@Entity
public class TingTag extends BaseEntity {
    @Column
    private Long tingId;

    @Column
    private Long tagId;

    public Long getTingId() {
        return tingId;
    }

    public void setTingId(Long tingId) {
        this.tingId = tingId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
