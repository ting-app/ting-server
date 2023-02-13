package ting.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ting.BaseTest;
import ting.entity.User;
import ting.repository.extend.ProgramRepositoryExtend;

public class ProgramRepositoryExtendTest extends BaseTest {
    @Autowired
    private ProgramRepositoryExtend programRepositoryExtend;

    @Test
    public void shouldCountPrograms() {
        User user = createUser(true, "password");

        createProgram(1, true, user.getId());
        createProgram(1, true, user.getId());
        createProgram(2, true, user.getId());

        Assertions.assertEquals(2, programRepositoryExtend.count(1, user.getId()));
        Assertions.assertEquals(1, programRepositoryExtend.count(2, user.getId()));
    }
}
