package com.xxx.api.apiscaffold.service.impl;

import com.xxx.api.apiscaffold.config.*;
import com.xxx.api.apiscaffold.service.Oauth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/30 15:28
 * @extra code change the world
 * @description
 */
@Service("oauth2Service")
public class Oauth2ServiceImpl implements Oauth2Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(Oauth2ServiceImpl.class);

    /**
     * 缓存 refresh_token，避免同时发起多个请求时不停刷新token
     */
    private final PersistentTokenCache<OAuth2Cookies> recentlyRefreshed = new PersistentTokenCache<>(10000L);

    @Autowired
    CustomConfig customConfig;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OAuth2Helper oAuth2Helper;

    @Override
    public OAuth2AccessToken sendPasswordGrant(String username, String password) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.set("username", username);
        formParams.set("password", password);
        formParams.set("grant_type", "password");
        formParams.set("client_id", customConfig.getSecurity().getClientId());
        formParams.set("client_secret", customConfig.getSecurity().getClientSecret());

        LOGGER.info("fetch access token from oauth2 server: {}", customConfig.getSecurity().getTokenUri());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formParams, httpHeaders);
        ResponseEntity<OAuth2AccessToken> responseEntity = restTemplate.postForEntity(customConfig.getSecurity().getTokenUri(), entity, OAuth2AccessToken.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            LOGGER.warn("fetch access token fail: status = {}", responseEntity.getStatusCode());
            LOGGER.warn("用户登陆失败: {}", username);
            return null;
        }

        return responseEntity.getBody();
    }

    @Override
    public OAuth2AccessToken sendRefreshGrant(String refreshToken) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
            formParams.set("refresh_token", refreshToken);
            formParams.set("grant_type", "refresh_token");
            formParams.set("client_id", customConfig.getSecurity().getClientId());
            formParams.set("client_secret", customConfig.getSecurity().getClientSecret());

            LOGGER.info("refresh access token from oauth2 server: {}", customConfig.getSecurity().getTokenUri());
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formParams, httpHeaders);
            ResponseEntity<OAuth2AccessToken> responseEntity = restTemplate.postForEntity(customConfig.getSecurity().getTokenUri(), entity, OAuth2AccessToken.class);
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                LOGGER.warn("refresh access token fail: status = {}", responseEntity.getStatusCode());
                LOGGER.warn("refresh access token fail: {}", refreshToken);
                return null;
            }

            return responseEntity.getBody();
        } catch (Exception e) {
            LOGGER.error("refresh token error: {}", e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public HttpServletRequest refreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken, boolean rememberMe) {
        if (!rememberMe && oAuth2Helper.isSessionExpired(refreshToken)) {
            LOGGER.warn("session has expired due to inactivity.");
            logout(request, response);
            return clearTokens(request);
        }
        OAuth2Cookies cookies = getCachedCookies(refreshToken);
        synchronized (cookies) {
            //check if we have a result from another thread already
            if (cookies.getAccessTokenCookie() == null) {            //no, we are first!
                LOGGER.info("refreshing token");
                OAuth2AccessToken accessToken = sendRefreshGrant(refreshToken);
                if (accessToken == null) {
                    LOGGER.warn("refresh token fail: {}", refreshToken);
                    return request;
                }
                LOGGER.info("refresh token success");
                oAuth2Helper.createCookies(request, accessToken, rememberMe, cookies);
                //add cookies to response to update browser
                cookies.addCookiesTo(response);
            } else {
                LOGGER.info("reusing cached refresh_token grant");
            }
            //replace cookies in original request with new ones
            CookieCollection requestCookies = new CookieCollection(request.getCookies());
            requestCookies.add(cookies.getAccessTokenCookie());
            requestCookies.add(cookies.getRefreshTokenCookie());
            return new CustomHttpServletRequestWrapper(request, requestCookies.toArray(),
                    cookies.getAccessTokenCookie().getValue(), cookies.getRefreshTokenCookie().getValue());
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        oAuth2Helper.clearCookies(request, response);
    }

    @Override
    public HttpServletRequest clearTokens(HttpServletRequest request) {
        Cookie[] cookies = oAuth2Helper.stripCookies(request.getCookies());
        return new CustomHttpServletRequestWrapper(request, cookies, "", "");
    }

    @Override
    public boolean ignoreRefreshUri(String uri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String ignore : customConfig.getSecurity().getIgnoreUris()) {
            if (pathMatcher.match(ignore, uri)) {
                return true;
            }
        }
        return false;
    }

    private OAuth2Cookies getCachedCookies(String refreshTokenValue) {
        synchronized (recentlyRefreshed) {
            OAuth2Cookies ctx = recentlyRefreshed.get(refreshTokenValue);
            if (ctx == null) {
                ctx = new OAuth2Cookies();
                recentlyRefreshed.put(refreshTokenValue, ctx);
            }
            return ctx;
        }
    }
}
