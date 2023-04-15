package dekiru.ting.repository;

import dekiru.ting.BaseTest;
import dekiru.ting.entity.User;
import dekiru.ting.repository.extend.ProgramRepositoryExtend;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
