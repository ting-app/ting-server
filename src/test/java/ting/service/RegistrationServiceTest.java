package ting.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ting.BaseTest;
import ting.entity.User;

public class RegistrationServiceTest extends BaseTest {
    @Autowired
    private RegistrationService registrationService;

    @MockBean
    private AwsSesService awsSesService;

    @Test
    public void shouldSendRegistrationConfirmEmail() {
        User user = createUser(false, "password");

        Mockito.doNothing().when(awsSesService).send(user.getEmail(), "title", "content");

        registrationService.sendRegistrationConfirmEmail(user);
    }
}
