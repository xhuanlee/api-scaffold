package com.xxx.api.apiscaffold.controller;

import com.xxx.api.apiscaffold.model.User;
import com.xxx.api.apiscaffold.model.request.LoginUser;
import com.xxx.api.apiscaffold.model.request.RegisterUser;
import com.xxx.api.apiscaffold.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/20 15:01
 * @extra code change the world
 * @description
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    UserService userService;

    @ApiOperation("登陆接口")
    @PostMapping("login")
    public ResponseEntity login(@ApiParam(value = "登录用户信息") @Validated @RequestBody LoginUser user) {
        return userService.login(user.getUsername(), user.getPassword(), user.getRememberMe());
    }

    @ApiOperation("登出接口")
    @PostMapping("logout")
    public ResponseEntity logout() {
        return userService.logout();
    }

    @ApiOperation("注册接口")
    @PostMapping("register")
    public ResponseEntity register(@ApiParam(value = "注册用户信息") @Validated @RequestBody RegisterUser user) {
        return userService.register(user);
    }

    @ApiOperation(value = "获取自己的详细信息", response = User.class)
    @GetMapping("info")
    public ResponseEntity userInfo() {
        return userService.getUserInfo(getUserId());
    }

    @ApiOperation("查询用户信息")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity getUserInfoById(@ApiParam(value = "用户id", required = true, example = "1") @PathVariable int id) {
        return userService.getUserInfo(id);
    }

}
