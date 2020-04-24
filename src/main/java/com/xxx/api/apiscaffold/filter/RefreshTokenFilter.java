package com.xxx.api.apiscaffold.filter;

import com.xxx.api.apiscaffold.config.OAuth2Helper;
import com.xxx.api.apiscaffold.service.Oauth2Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/31 11:18
 * @extra code change the world
 * @description
 * 自动刷新 access_token 的拦截器，如果请求头或cookie中包含 refresh_token，并且请求头或cookie中的 access_token 快要过期了，
 * 就会自动去 refresh token，如果 refresh 成功，则重新创建 HttpServletRequest，将新的 access_token 和
 * refresh_token 设置到请求头和cookie中，同时设置到响应头中，前端应该判断响应头中是否有 token 信息，如果有，需要刷新
 * 缓存中的 token 信息。(如果是浏览器的话不需要管请求头，只需要带上 cookie 就可以了，后台会自动处理 access_token 和 refresh_token)
 */
public class RefreshTokenFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenFilter.class);

    private static final int REFRESH_WINDOW_SECS = 30;

    private Oauth2Service oauth2Service;

    private TokenStore tokenStore;

    public RefreshTokenFilter(Oauth2Service oauth2Service, TokenStore tokenStore) {
        this.oauth2Service = oauth2Service;
        this.tokenStore = tokenStore;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        LOGGER.info("refresh token filter executing: {}", httpServletRequest.getRequestURI());
        try {
            if (!oauth2Service.ignoreRefreshUri(httpServletRequest.getRequestURI())) {
                httpServletRequest = refreshTokensIfExpiring(httpServletRequest, httpServletResponse);
            } else {
                LOGGER.info("{} is in ignore list, skip check access_token and do not need to refresh token", httpServletRequest.getRequestURI());
            }
        } catch (ClientAuthenticationException ex) {
            LOGGER.warn("Security exception: could not refresh tokens", ex);
        }
        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    public HttpServletRequest refreshTokensIfExpiring(HttpServletRequest httpServletRequest, HttpServletResponse
            httpServletResponse) {
        LOGGER.info("ready to refresh token auto if refresh_token exists.");
        HttpServletRequest newHttpServletRequest = httpServletRequest;
        //get the refresh token cookie and, if present, request new tokens
        String refreshToken = OAuth2Helper.getRefreshToken(httpServletRequest);
        if (StringUtils.isNotBlank(refreshToken)) {
            LOGGER.info("get refresh_token success, if access_token expired, we will refresh token auto");
            //get access token from cookie
            String accessToken = OAuth2Helper.getAccessToken(httpServletRequest);
            if (mustRefreshToken(accessToken)) {        //we either have no access token, or it is expired, or it is about to expire
                try {
                    LOGGER.info("access_token is null or expired, refreshing...");
                    boolean rememberMe = OAuth2Helper.isRememberMe(httpServletRequest);
                    // refresh token
                    newHttpServletRequest = oauth2Service.refreshToken(httpServletRequest, httpServletResponse, refreshToken, rememberMe);
                } catch (HttpClientErrorException ex) {
                    LOGGER.error("auto refresh token error: {}", ex.getMessage());
                    LOGGER.error(ex.getMessage(), ex);
                    throw new UnauthorizedClientException("unauthorized");
                }
            }
        }

        return newHttpServletRequest;
    }

    private boolean mustRefreshToken(String accessToken) {
        if (accessToken == null) {
            return true;
        }
        OAuth2AccessToken token = tokenStore.readAccessToken(accessToken);
        //check if token is expired or about to expire
        if (token.isExpired() || token.getExpiresIn() < REFRESH_WINDOW_SECS) {
            return true;
        }
        return false;       //access token is still fine
    }
}
