package com.xxx.api.apiscaffold.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/4/15 15:40
 * @extra code change the world
 * @description
 */
@ApiModel("重置密码")
public class ResetPasswordReq {

    @ApiModelProperty(value = "邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @ApiModelProperty(value = "签名", required = true)
    @NotBlank(message = "签名不能为空")
    private String sign;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ResetPasswordReq{" +
                "email='" + email + '\'' +
                ", sign='" + sign + '\'' +
                ", password=[******]" +
                '}';
    }
}
