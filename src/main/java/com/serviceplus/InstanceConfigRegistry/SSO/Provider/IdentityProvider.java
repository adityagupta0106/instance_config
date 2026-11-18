package com.serviceplus.InstanceConfigRegistry.SSO.Provider;

import com.serviceplus.InstanceConfigRegistry.Entity.Configuration;
import jakarta.servlet.http.HttpServletRequest;

public interface IdentityProvider {

    /**
     * Unique provider.
     * Example:
     * JANPARICHAY
     * DIGILOCKER
     */
    SSOProvider getProvider();

    Configuration registerService(HttpServletRequest request, Configuration configuration);
}