package com.xxx.api.apiscaffold.config;

import com.xxx.api.apiscaffold.service.Oauth2Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/30 16:31
 * @extra code change the world
 * @description 拦截异常，如果是 401 unauthorized，直接自动 refresh token，并将新的 accessToken 放到 response header 中。
 * app 和 web 前端处理 response header 中的 access token.
 */
@Component
public class SecurityAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAuthenticationEntryPoint.class);

    @Autowired
    Oauth2Service oauth2Service;

    private WebResponseExceptionTranslator<?> exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        try {
            LOGGER.error(authException.getMessage());
            ResponseEntity<?> result = exceptionTranslator.translate(authException);
            String refreshToken = request.getHeader("refresh_token");
            // 如果请求头中的 refresh_token 为空，再去获取参数中的 refresh_token
            if (StringUtils.isBlank(refreshToken)) {
                refreshToken = request.getParameter("refresh_token");
            }
            // 如果 refresh_token 还为空，再去 cookie 中获取 refresh_token
            if (StringUtils.isBlank(refreshToken)) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null && cookies.length > 0) {
                    for (Cookie cookie : cookies) {
                        if ("refresh_token".equals(cookie.getName())) {
                            LOGGER.info("get refresh_token from cookie success.");
                            refreshToken = cookie.getValue();
                            break;
                        }
                    }
                }
            }

            //只有请求中携带了 refresh_token 时才可以刷新
            if (StringUtils.isNotBlank(refreshToken) && result.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                //如果是验证失败，尝试利用refresh_token去刷新access_token，如果刷新成功，则继续之前的请求，否则直接返回.
                OAuth2AccessToken accessToken = oauth2Service.sendRefreshGrant(refreshToken);
                if (accessToken != null) {
                    //成功刷新 access_token
                    LOGGER.info("refresh access_token success, make request({}) again", request.getRequestURI());
                    //更新 request header 中的 access_token 和 refresh_token，再次发起请求;
                    //将新的 access_token 放到 response header 中
                    response.setHeader("access_token", accessToken.getValue());
                    response.setHeader("access_type", accessToken.getTokenType());
                    response.setHeader("refresh_token", accessToken.getRefreshToken().getValue());
                    //重新发起请求
                    //这里重新发起请求对于有权限认证的是有问题的，因为这里是在spring security authenticate 之后
                    //所以下面的 getAuthentication 是null，也就是说，如果请求里面如果没有对权限的验证没有问题
                    //一旦有类似 @PreAuthorize("hasRole('SUPER')") 的权限管控，就会报 authentication object is null 的错误.
                    //所以，这里现在还是返回 401，前台如果收到 401 的错误，直接再请求一次，后续有好方法再改进
                    SecurityContextHolder.getContext().getAuthentication();
                    request.getRequestDispatcher(request.getRequestURI()).forward(request, response);
                    //super.commence(request, response, authException);
                }
            }
        } catch (Exception e) {
            LOGGER.error("auto refresh access_token error.");
            LOGGER.error(e.getMessage(), e);
        }

        // 如果刷新 access_token 异常，还是走原来逻辑
        super.commence(request, response, authException);
    }

}
