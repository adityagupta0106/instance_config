package com.serviceplus.InstanceConfigRegistry.SSO.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sso.providers.janparichay")
public class JanParichayProperties {

    private String serviceOnboardingUrl;

    private String redirectUri;

    private String restAuthId;

    private String hmacSecret;

    public String getServiceOnboardingUrl() {
        return serviceOnboardingUrl;
    }

    public void setServiceOnboardingUrl(String serviceOnboardingUrl) {
        this.serviceOnboardingUrl = serviceOnboardingUrl;
    }

    public String getHmacSecret() {
        return hmacSecret;
    }

    public void setHmacSecret(String hmacSecret) {
        this.hmacSecret = hmacSecret;
    }

    public String getRestAuthId() {
        return restAuthId;
    }

    public void setRestAuthId(String restAuthId) {
        this.restAuthId = restAuthId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}