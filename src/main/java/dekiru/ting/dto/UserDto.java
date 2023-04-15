package dekiru.ting.dto;

import java.io.Serializable;

/**
 * The data transfer object that represents a user.
 */
public class UserDto implements Serializable {
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
