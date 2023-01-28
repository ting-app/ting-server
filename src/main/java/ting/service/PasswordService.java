package ting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ting.config.TingConfig;

import java.security.SecureRandom;

/**
 * The service that manages user's password.
 */
@Service
public class PasswordService {
    @Autowired
    private TingConfig tingConfig;

    /**
     * Check if the raw password matches the encrypted password.
     *
     * @param rawPassword       The raw password
     * @param encryptedPassword The encrypted password
     * @return True if matches, false otherwise
     */
    public boolean matches(String rawPassword, String encryptedPassword) {
        BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();

        return cryptPasswordEncoder.matches(rawPassword, encryptedPassword);
    }

    /**
     * Encrypt password.
     *
     * @param password The password to encrypt
     * @return Encrypted password
     */
    public String encrypt(String password) {
        BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder(
                tingConfig.getPasswordStrength(), new SecureRandom());

        return cryptPasswordEncoder.encode(password);
    }
}
