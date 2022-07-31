package ting.dto;

import ting.validation.Login;
import ting.validation.Register;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserCredential {
    @NotBlank(message = "姓名不能为空", groups = {Register.class, Login.class})
    @Size(max = 20, message = "姓名不能超过20个字符", groups = {Register.class})
    private String name;

    @NotBlank(message = "密码不能为空", groups = {Register.class, Login.class})
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间", groups = {Register.class})
    private String password;

    @NotBlank(message = "确认密码不能为空", groups = {Register.class})
    private String confirmPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
