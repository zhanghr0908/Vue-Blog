package com.ltj.blog.common.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录数据Vo
 */
@Data
public class LoginDto extends AbstractMethodError implements Serializable  {

    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;

}
