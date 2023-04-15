package dekiru.ting.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import dekiru.ting.BaseTest;

public class PasswordServiceTest extends BaseTest {
    @Autowired
    private PasswordService passwordService;

    @Test
    public void shouldMatchPassword() {
        String password = "abc";
        String encryptedPassword = passwordService.encrypt(password);

        Assertions.assertTrue(passwordService.matches(password, encryptedPassword));
    }
}
