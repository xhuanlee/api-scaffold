package com.xxx.api.apiscaffold.config;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/31 12:03
 * @extra code change the world
 * @description
 * 参考 OAuth2AuthenticationProcessingFilter 中解析 token 的方法 BearerTokenExtractor.extractToken
 * 是通过 HttpServletRequest.getHeaders(name)获取 Authorization 信息，如果请求头中获取不到，再获取参数中的 access_token
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final String AUTHORIZATION = "Authorization";

    private Map<String, String> headers;

    private Cookie[] cookies;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.headers = new HashMap<>();
    }

    public CustomHttpServletRequestWrapper(HttpServletRequest request, Cookie[] cookies, String accessToken, String refreshToken) {
        super(request);
        this.cookies = cookies;
        this.headers = new HashMap<>();
        this.headers.put(AUTHORIZATION, String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, accessToken));
        this.headers.put(OAuth2AccessToken.REFRESH_TOKEN, refreshToken);
    }

    public CustomHttpServletRequestWrapper(HttpServletRequest request, String accessToken, String refreshToken) {
        super(request);
        this.headers = new HashMap<>();
        this.headers.put(AUTHORIZATION, String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, accessToken));
        this.headers.put(OAuth2AccessToken.REFRESH_TOKEN, refreshToken);
    }

    public void putHeader(String name, String value) {
        this.headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        if (this.headers.containsKey(name)) {
            return this.headers.get(name);
        }
        return super.getHeader(name);
    }

    @Override
    public Cookie[] getCookies() {
        if (this.cookies != null) {
            return this.cookies;
        }
        return super.getCookies();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (this.headers.containsKey(name)) {
            if ("".equals(this.headers.get(name).trim())) {
                return new Enumeration<String>() {
                    @Override
                    public boolean hasMoreElements() {
                        return false;
                    }
                    @Override
                    public String nextElement() {
                        return null;
                    }
                };
            }

            return new Enumeration<String>() {
                boolean hasRead = false;
                @Override
                public boolean hasMoreElements() {
                    return !hasRead;
                }

                @Override
                public String nextElement() {
                    if (!hasRead) {
                        hasRead = true;
                        return headers.get(name);
                    } else {
                        throw new NoSuchElementException("no more element");
                    }
                }
            };
        }

        return super.getHeaders(name);
    }
}
