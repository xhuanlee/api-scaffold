package com.xxx.api.apiscaffold.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/03/20 11:50
 * @extra code change the world
 * @description
 */
@Configuration
@ConfigurationProperties(prefix = "custom", ignoreInvalidFields = true)
public class CustomConfig {

    private Security security = new Security();

    private Email email = new Email();

    private Activation activation = new Activation();

    private ResetPassword resetPassword = new ResetPassword();

    // swagger auth url
    private String swaggerTokenUrl;

    public Security getSecurity() {
        return security;
    }

    public Email getEmail() {
        return email;
    }

    public Activation getActivation() {
        return activation;
    }

    public ResetPassword getResetPassword() {
        return resetPassword;
    }

    public String getSwaggerTokenUrl() {
        return swaggerTokenUrl;
    }

    public void setSwaggerTokenUrl(String swaggerTokenUrl) {
        this.swaggerTokenUrl = swaggerTokenUrl;
    }

    public static class Security {
        private String clientId;
        private String clientSecret;
        private int maxSession = 1;
        // default 10 minutes(10 * 60)
        private int accessTokenValidity = 600;
        // default 1 week(7 * 24 * 60 * 60)
        private int refreshTokenValidity = 604800;
        // default 30 minutes(30 * 60)
        private int sessionTimeoutInSeconds = 1800;
        // token uri
        private String tokenUri = "http://127.0.0.1:8080/oauth/token";
        // resources - default ainanance-api
        private String[] resourceIds = new String[] {"api-scaffold"};
        private String[] permitUris = new String[] {};
        private String[] ignoreUris = new String[] {};
        private String cookieDomain;
        private List<String> adminRoles;
        private String tokenSignKey;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public int getMaxSession() {
            return maxSession;
        }

        public void setMaxSession(int maxSession) {
            this.maxSession = maxSession;
        }

        public int getAccessTokenValidity() {
            return accessTokenValidity;
        }

        public void setAccessTokenValidity(int accessTokenValidity) {
            this.accessTokenValidity = accessTokenValidity;
        }

        public int getRefreshTokenValidity() {
            return refreshTokenValidity;
        }

        public void setRefreshTokenValidity(int refreshTokenValidity) {
            this.refreshTokenValidity = refreshTokenValidity;
        }

        public int getSessionTimeoutInSeconds() {
            return sessionTimeoutInSeconds;
        }

        public void setSessionTimeoutInSeconds(int sessionTimeoutInSeconds) {
            this.sessionTimeoutInSeconds = sessionTimeoutInSeconds;
        }

        public String[] getPermitUris() {
            return permitUris;
        }

        public void setPermitUris(String[] permitUris) {
            this.permitUris = permitUris;
        }

        public String getTokenUri() {
            return tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }

        public String[] getResourceIds() {
            return resourceIds;
        }

        public void setResourceIds(String[] resourceIds) {
            this.resourceIds = resourceIds;
        }

        public String[] getIgnoreUris() {
            return ignoreUris;
        }

        public void setIgnoreUris(String[] ignoreUris) {
            this.ignoreUris = ignoreUris;
        }

        public String getCookieDomain() {
            return cookieDomain;
        }

        public void setCookieDomain(String cookieDomain) {
            this.cookieDomain = cookieDomain;
        }

        public List<String> getAdminRoles() {
            return adminRoles;
        }

        public void setAdminRoles(List<String> adminRoles) {
            this.adminRoles = adminRoles;
        }

        public String getTokenSignKey() {
            return tokenSignKey;
        }

        public void setTokenSignKey(String tokenSignKey) {
            this.tokenSignKey = tokenSignKey;
        }
    }

    public static class Email {
        private String host;
        private int port;
        private boolean useSsl;
        private String username;
        private String password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isUseSsl() {
            return useSsl;
        }

        public void setUseSsl(boolean useSsl) {
            this.useSsl = useSsl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Activation {
        private String subject;
        private String link;
        private String redirect;
        private String signPlaceholder;
        private String emailPlaceholder;
        private String linkPlaceholder;
        private String emailTemplatePath;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getRedirect() {
            return redirect;
        }

        public void setRedirect(String redirect) {
            this.redirect = redirect;
        }

        public String getSignPlaceholder() {
            return signPlaceholder;
        }

        public void setSignPlaceholder(String signPlaceholder) {
            this.signPlaceholder = signPlaceholder;
        }

        public String getEmailPlaceholder() {
            return emailPlaceholder;
        }

        public void setEmailPlaceholder(String emailPlaceholder) {
            this.emailPlaceholder = emailPlaceholder;
        }

        public String getLinkPlaceholder() {
            return linkPlaceholder;
        }

        public void setLinkPlaceholder(String linkPlaceholder) {
            this.linkPlaceholder = linkPlaceholder;
        }

        public String getEmailTemplatePath() {
            return emailTemplatePath;
        }

        public void setEmailTemplatePath(String emailTemplatePath) {
            this.emailTemplatePath = emailTemplatePath;
        }
    }

    public static class ResetPassword {
        private String subject;
        private String link;
        private String signPlaceholder;
        private String emailPlaceholder;
        private String linkPlaceholder;
        private String emailTemplatePath;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getSignPlaceholder() {
            return signPlaceholder;
        }

        public void setSignPlaceholder(String signPlaceholder) {
            this.signPlaceholder = signPlaceholder;
        }

        public String getEmailPlaceholder() {
            return emailPlaceholder;
        }

        public void setEmailPlaceholder(String emailPlaceholder) {
            this.emailPlaceholder = emailPlaceholder;
        }

        public String getLinkPlaceholder() {
            return linkPlaceholder;
        }

        public void setLinkPlaceholder(String linkPlaceholder) {
            this.linkPlaceholder = linkPlaceholder;
        }

        public String getEmailTemplatePath() {
            return emailTemplatePath;
        }

        public void setEmailTemplatePath(String emailTemplatePath) {
            this.emailTemplatePath = emailTemplatePath;
        }
    }
}
