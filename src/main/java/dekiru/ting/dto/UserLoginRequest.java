package dekiru.ting.dto;

import javax.validation.constraints.NotBlank;

/**
 * The request entity to login.
 */
public class UserLoginRequest {
    @NotBlank(message = "姓名或邮箱地址不能为空")
    private String nameOrEmail;

    @NotBlank(message = "密码不能为空")
    private String password;

    public String getNameOrEmail() {
        return nameOrEmail;
    }

    public void setNameOrEmail(String nameOrEmail) {
        this.nameOrEmail = nameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
