package ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class User extends BaseEntity {
    @Column
    private String name;
}
