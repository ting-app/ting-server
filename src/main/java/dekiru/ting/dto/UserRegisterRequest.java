package dekiru.ting.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * The request entity to register a new user.
 */
public class UserRegisterRequest {
    @NotBlank(message = "姓名不能为空")
    @Size(max = 20, message = "姓名不能超过20个字符")
    private String name;

    @NotBlank(message = "邮箱地址不能为空")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
