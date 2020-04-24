package com.xxx.api.apiscaffold.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Holds the access token and refresh token cookies.
 */
public class OAuth2Cookies {
    private Cookie accessTokenCookie;
    private Cookie refreshTokenCookie;

    public Cookie getAccessTokenCookie() {
        return accessTokenCookie;
    }

    public Cookie getRefreshTokenCookie() {
        return refreshTokenCookie;
    }

    public void setCookies(Cookie accessTokenCookie, Cookie refreshTokenCookie) {
        this.accessTokenCookie = accessTokenCookie;
        this.refreshTokenCookie = refreshTokenCookie;
    }

    /**
     * Add the access token and refresh token as cookies to the response after successful authentication.
     *
     * @param response the response to add them to.
     */
    public void addCookiesTo(HttpServletResponse response) {
        response.addCookie(getAccessTokenCookie());
        response.addCookie(getRefreshTokenCookie());
        response.setHeader(getAccessTokenCookie().getName(), getAccessTokenCookie().getValue());
        response.setHeader(getRefreshTokenCookie().getName(), getAccessTokenCookie().getValue());
    }
}
