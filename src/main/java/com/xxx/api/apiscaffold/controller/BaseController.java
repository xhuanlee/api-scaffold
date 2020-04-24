package com.xxx.api.apiscaffold.controller;

import com.xxx.api.apiscaffold.config.CustomConfig;
import com.xxx.api.apiscaffold.model.SecurityUser;
import com.xxx.api.apiscaffold.service.UserService;
import com.xxx.api.apiscaffold.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/4/3 09:47
 * @extra code change the world
 * @description
 */
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    CustomConfig customConfig;

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isAdmin() {
        Collection<? extends GrantedAuthority> authorities = getAuthentication().getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (SecurityUtils.isCurrentUserInRole(authority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    public Integer getUserId() {
        try {
            LOGGER.debug("get user id: {}", getRequestUri());
            Object principal = getAuthentication().getPrincipal();
            LOGGER.debug("security principal: {}", principal);
            if (principal instanceof SecurityUser) {
                LOGGER.debug("security principal is instanceof security user.");
                return ((SecurityUser) principal).getId();
            }
            return userService.getUserInfo(principal.toString()).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getRequestUri() {
        if (request == null) {
            return null;
        }
        return request.getRequestURI();
    }

}
