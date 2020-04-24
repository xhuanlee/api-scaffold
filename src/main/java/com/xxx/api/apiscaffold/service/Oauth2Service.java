package com.xxx.api.apiscaffold.service;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/30 15:26
 * @extra code change the world
 * @description
 */
public interface Oauth2Service {

    OAuth2AccessToken sendPasswordGrant(String username, String password);

    OAuth2AccessToken sendRefreshGrant(String refreshToken);

    HttpServletRequest refreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken, boolean rememberMe);

    void logout(HttpServletRequest request, HttpServletResponse response);

    HttpServletRequest clearTokens(HttpServletRequest request);

    boolean ignoreRefreshUri(String uri);
}
