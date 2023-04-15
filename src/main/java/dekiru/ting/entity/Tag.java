package dekiru.ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * The tag entity.
 */
@Entity
public class Tag extends BaseEntity {
    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
