package com.xxx.api.apiscaffold.config;

import com.xxx.api.apiscaffold.service.Oauth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/03/20 11:40
 * @extra code change the world
 * @description
 */
@Configuration
@EnableResourceServer
public class Oauth2ResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    CustomConfig customConfig;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    Oauth2Service oauth2Service;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("api-scaffold").tokenExtractor(new CookieTokenExtractor());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .requestMatchers().anyRequest()
                .and()
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers(customConfig.getSecurity().getIgnoreUris()).permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/**").authenticated()
                .and().apply(refreshTokenFilterConfigurer());
    }

    private RefreshTokenFilterConfigurer refreshTokenFilterConfigurer() {
        return new RefreshTokenFilterConfigurer(oauth2Service, tokenStore);
    }
}
