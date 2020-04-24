package com.xxx.api.apiscaffold.config;

import com.xxx.api.apiscaffold.filter.RefreshTokenFilter;
import com.xxx.api.apiscaffold.service.Oauth2Service;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/31 11:21
 * @extra code change the world
 * @description
 */
public class RefreshTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private Oauth2Service oauth2Service;

    private TokenStore tokenStore;

    public RefreshTokenFilterConfigurer(Oauth2Service oauth2Service, TokenStore tokenStore) {
        this.oauth2Service = oauth2Service;
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        RefreshTokenFilter customFilter = new RefreshTokenFilter(oauth2Service, tokenStore);
        builder.addFilterBefore(customFilter, OAuth2AuthenticationProcessingFilter.class);
    }

}
