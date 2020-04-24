package com.xxx.api.apiscaffold.service.impl;

import com.xxx.api.apiscaffold.config.CustomConfig;
import com.xxx.api.apiscaffold.config.OAuth2Cookies;
import com.xxx.api.apiscaffold.config.OAuth2Helper;
import com.xxx.api.apiscaffold.dao.RoleMapper;
import com.xxx.api.apiscaffold.dao.UserMapper;
import com.xxx.api.apiscaffold.model.Role;
import com.xxx.api.apiscaffold.model.SecurityUser;
import com.xxx.api.apiscaffold.model.User;
import com.xxx.api.apiscaffold.model.example.UserExample;
import com.xxx.api.apiscaffold.model.request.RegisterUser;
import com.xxx.api.apiscaffold.service.*;
import com.xxx.api.apiscaffold.util.ContextUtil;
import com.xxx.api.apiscaffold.util.RandomStringUtil;
import com.xxx.api.apiscaffold.util.ResponseEntityBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/23 14:16
 * @extra code change the world
 * @description
 */
@Transactional
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    CustomConfig customConfig;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserMapper mapper;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    Oauth2Service oauth2Service;

    @Autowired
    OAuth2Helper oAuth2Helper;

    @Autowired
    RoleService roleService;

    @Override
    public ResponseEntity login(String username, String password, Integer rememberMe) {
        LOGGER.info("user login: {}, password: ******", username);

        try {
            OAuth2AccessToken accessToken = oauth2Service.sendPasswordGrant(username, password);
            LOGGER.info("user login success: {}", username);
            LOGGER.debug("user login access token: {}", accessToken);
            LOGGER.info("user login access token: {}", accessToken.getValue());
            User user = getUserInfo(username);

            LOGGER.info("save token to cookie: {}", username);
            OAuth2Cookies cookies = new OAuth2Cookies();
            boolean remember = (rememberMe != null && rememberMe == 1);
            oAuth2Helper.createCookies(ContextUtil.getRequest(), accessToken, remember, cookies);
            cookies.addCookiesTo(ContextUtil.getResponse());

            ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
            builder.addData("user", user);
            return builder.build();
        } catch (HttpClientErrorException e) {
            LOGGER.error("登陆失败: statusCode = {}, statusText = {}, message = {}, body = {}, cause = {}",
                    e.getStatusCode(), e.getStatusText(), e.getMessage(), e.getResponseBodyAsString(), e.getCause());
            String body = e.getResponseBodyAsString();
            if (StringUtils.isNotBlank(body) && body.contains("User is disabled")) {
                return ResponseEntityBuilder.error(HttpStatus.UNAUTHORIZED.value(), "请点击邮箱中的激活链接激活用户");
            }
            return ResponseEntityBuilder.error(HttpStatus.BAD_REQUEST.value(), "用户名或密码错误");
        } catch (Exception e) {
            LOGGER.error("登陆异常: username = {}, error = {}", username, e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }

        return ResponseEntityBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器异常");
    }

    @Override
    public ResponseEntity logout() {
        oauth2Service.logout(ContextUtil.getRequest(), ContextUtil.getResponse());

        return ResponseEntityBuilder.builder(HttpStatus.OK.value(), "success").build();
    }

    @Override
    public ResponseEntity register(RegisterUser user) {
        LOGGER.info("user register: {}", user);

        // 判断邮箱是否已注册
        User dbUser = getUserInfo(user.getUsername());
        if (dbUser != null) {
            return ResponseEntityBuilder.error(HttpStatus.EXPECTATION_FAILED.value(), String.format("%s已经注册，请直接登陆", user.getUsername()));
        }

        User u = new User();
        BeanUtils.copyProperties(user, u);
        u.setPassword(passwordEncoder.encode(user.getPassword()));

        // 保存用户信息
        int count = mapper.insertSelective(u);
        if (count <= 0) {
            throw new RuntimeException("注册失败");
        }

        return ResponseEntityBuilder.builder().message("注册成功").build();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = getUserInfo(s);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("%s is not registered yet!", s));
        }

        List<Role> roles = roleService.getRoleByUser(user.getId());
        List<GrantedAuthority> authorities = null;
        if (roles != null && !roles.isEmpty()) {
            authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
        }
        SecurityUser u = new SecurityUser(user.getUsername(), user.getPassword(), user.getStatus() == 1, authorities);
        u.setId(user.getId());
        return u;
    }

    @Override
    public ResponseEntity getUserInfo(int id) {
        LOGGER.info("query user info: {}", id);
        User user = mapper.selectByPrimaryKey(id);
        if (user == null) {
            LOGGER.warn("user not exists: ", id);
            return ResponseEntityBuilder.error(HttpStatus.NOT_FOUND.value(), "用户信息不存在");
        }

        LOGGER.info("query user info success: {}", id);
        LOGGER.debug("query user info success: {}", user);

        return ResponseEntityBuilder.setEntry(user).build();
    }

    @Override
    public User getUserInfoById(int id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public User getUserInfo(String username) {
        LOGGER.info("query user from username: {}", username);
        UserExample query = new UserExample();
        query.createCriteria().andUsernameEqualTo(username);
        List<User> list = mapper.selectByExample(query);
        if (list == null || list.isEmpty()) {
            LOGGER.warn("query user from username fail: {}", username);
            return null;
        }

        LOGGER.info("query user from username success: {}", username);
        return list.get(0);
    }

    @Override
    public boolean existsInviteCode(String code) {
        User user = getUserInfoByInviteCode(code);
        return user != null;
    }

    @Override
    public User getUserInfoByInviteCode(String inviteCode) {
        UserExample query = new UserExample();
        List<User> list = mapper.selectByExample(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }

    @Override
    public boolean isUserIdValid(int userId) {
        return mapper.selectByPrimaryKey(userId) != null;
    }

    public String getNewInviteCode() {
        LOGGER.info("generate new invite code.");
        String code = RandomStringUtil.newRandomString(6);
        boolean exists = existsInviteCode(code);
        int i = 1;
        while (exists) {
            LOGGER.info("{} already exists, generate a new one.", code);
            code = RandomStringUtil.newRandomString(6);
            exists = existsInviteCode(code);
            i++;
        }
        LOGGER.info("generate new invite code success({} times): {}", i, code);

        return code;
    }
}
