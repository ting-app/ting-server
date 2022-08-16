package ting.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The service that manages user's password.
 */
@Service
public class PasswordService {
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
}
