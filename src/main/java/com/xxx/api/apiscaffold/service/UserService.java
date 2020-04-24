package com.xxx.api.apiscaffold.service;

import com.xxx.api.apiscaffold.model.User;
import com.xxx.api.apiscaffold.model.request.RegisterUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/20 14:48
 * @extra code change the world
 * @description
 */
public interface UserService extends UserDetailsService {

    ResponseEntity login(String username, String password, Integer rememberMe);

    ResponseEntity logout();

    ResponseEntity register(RegisterUser user);

    ResponseEntity getUserInfo(int id);

    User getUserInfoById(int id);

    User getUserInfo(String username);

    User getUserInfoByInviteCode(String inviteCode);

    boolean existsInviteCode(String code);

    boolean isUserIdValid(int userId);

}
