package ting.dto;

import java.io.Serializable;

/**
 * The data transfer object that represents a user.
 */
public class UserDto implements Serializable {
    private long id;

    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
