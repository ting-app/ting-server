package ting.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;

/**
 * The user entity.
 */
@Entity
public class User extends BaseEntity {
    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String encryptedPassword;

    @Column
    private boolean verified;

    @Column
    private Instant createdAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
