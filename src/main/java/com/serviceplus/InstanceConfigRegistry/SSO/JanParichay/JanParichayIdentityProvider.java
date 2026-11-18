package com.serviceplus.InstanceConfigRegistry.SSO.JanParichay;

import com.serviceplus.InstanceConfigRegistry.Entity.Configuration;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.ExternalApiClient.EsbClient;
import com.serviceplus.InstanceConfigRegistry.SSO.Properties.JanParichayProperties;
import com.serviceplus.InstanceConfigRegistry.SSO.Provider.IdentityProvider;
import com.serviceplus.InstanceConfigRegistry.SSO.Provider.SSOProvider;
import com.serviceplus.InstanceConfigRegistry.dto.IntegrationRequest;
import com.serviceplus.InstanceConfigRegistry.dto.IntegrationResponse;
import com.serviceplus.InstanceConfigRegistry.dto.JanParichayServiceRegistrationResponse;
import com.serviceplus.InstanceConfigRegistry.dto.ServiceRegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.BASIC_TAB;
import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.JANPARICHAY_TAB;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.*;

@Component
public class JanParichayIdentityProvider implements IdentityProvider {

    private static final Logger log = LoggerFactory.getLogger("tenantConfiguration");

    @Value("${app.environment}")
    private String environment;

    private final JanParichayProperties properties;
    private final EsbClient esbClient;

    public JanParichayIdentityProvider(JanParichayProperties properties, EsbClient esbClient) {
        this.properties = properties;
        this.esbClient = esbClient;
    }

    @Override
    public SSOProvider getProvider() {
        return SSOProvider.JANPARICHAY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration registerService(HttpServletRequest request, Configuration configuration) {

        Map<String, Object> basic = (Map<String, Object>) configuration.getData().get(BASIC_TAB);

        ServiceRegistrationRequest serviceRequest = buildRequest(configuration, basic);

        long ttl = System.currentTimeMillis();

        String signature = JanParichaySignatureUtil.generateSignature(

                serviceRequest.getServiceId(),
                ttl,
                properties.getServiceOnboardingUrl(),
                serviceRequest.getServiceId(),
                properties.getHmacSecret(),
                serviceRequest.getServiceId(),
                serviceRequest.getDescription(),
                serviceRequest.getDisplayName(),
                String.valueOf(serviceRequest.getEnforceMultiFactor()),
                String.valueOf(serviceRequest.getForceMultiFactor()),
                serviceRequest.getHomeUrl(),
                String.valueOf(serviceRequest.getLoginType()),
                serviceRequest.getLoginUrl(),
                serviceRequest.getLogoutUrl(),
                serviceRequest.getServiceCategory(),
                serviceRequest.getServiceState(),
                serviceRequest.getEnvironment(),
                serviceRequest.getSubService()
        );

        Map<String, String> headers = new HashMap<>();

        headers.put("RestAuthID", properties.getRestAuthId());
        headers.put("TTL", String.valueOf(ttl));
        headers.put("Signature", signature);

        String prodUrls = (String) basic.get("PROD_URL");
        List<String> prodDomains = splitString(prodUrls);

        if (prodDomains.isEmpty()) {
            throw new SPRuntimeError("At least one PROD_URL is required", HttpStatus.BAD_REQUEST);
        }

        String primaryProdDomain = prodDomains.getFirst();
        headers.put("sp-client-domain", primaryProdDomain);

        String payload = entityToString(serviceRequest);

        log.info("Calling JanParichay Service Registration API.");
        log.info("URL={}", properties.getServiceOnboardingUrl());
        log.info("Headers={}", headers);
        log.info("Payload={}", payload);

        IntegrationRequest integrationRequest = new IntegrationRequest();

        integrationRequest.setProvider(SSOProvider.JANPARICHAY.name());
        integrationRequest.setOperation("REGISTRATION");
        integrationRequest.setUrl(properties.getServiceOnboardingUrl());
        integrationRequest.setMethod(HttpMethod.POST.name());
        integrationRequest.setProtocol("REST");
        integrationRequest.setContentType(MediaType.APPLICATION_JSON_VALUE);
        integrationRequest.setHeaders(headers);
        integrationRequest.setBody(payload);
        log.info("Calling ESB for JanParichay service registration. serviceId={}", serviceRequest.getServiceId());

        IntegrationResponse integrationResponse;

        try {
            integrationResponse = esbClient.invoke(integrationRequest);

        } catch (Exception ex) {

            log.error("ESB invocation failed for JanParichay service registration. serviceId={}", serviceRequest.getServiceId(), ex);
            throw new SPRuntimeError("Failed to register service with JanParichay.", HttpStatus.BAD_GATEWAY);
        }

        if (integrationResponse == null) {
            throw new SPRuntimeError("Empty response received from JanParichay.", HttpStatus.BAD_GATEWAY);
        }

        log.info("JanParichay service registration response received through ESB. " + "status={}, serviceId={}", integrationResponse.getStatusCode(), serviceRequest.getServiceId());

        if (!integrationResponse.isSuccess()) {

            log.error("JanParichay service registration failed. HTTP status={}, serviceId={}", integrationResponse.getStatusCode(), serviceRequest.getServiceId());
            throw new SPRuntimeError("JanParichay service registration failed.", HttpStatus.BAD_GATEWAY);
        }

        String responseBody = String.valueOf(integrationResponse.getBody());

        JanParichayServiceRegistrationResponse jpResponse = (JanParichayServiceRegistrationResponse) stringToEntityUsingType(responseBody, JanParichayServiceRegistrationResponse.class);

        log.info("Parsed JanParichay Response={}", entityToString(jpResponse));

        if (jpResponse == null) {
            throw new SPRuntimeError("Empty response received from JanParichay.", HttpStatus.BAD_GATEWAY);
        }

        if (!"success".equalsIgnoreCase(jpResponse.getStatus())) {
            throw new SPRuntimeError(jpResponse.getMsg(), HttpStatus.BAD_GATEWAY);
        }

        if (jpResponse.getData() == null || jpResponse.getData().getClientId() == null
                || jpResponse.getData().getClientSecret() == null || jpResponse.getData().getServiceType() == null) {

            log.error("JanParichay returned success but registration data is missing. " + "ServiceId={}, Response={}",
                    serviceRequest.getServiceId(), entityToString(jpResponse));

            throw new SPRuntimeError("JanParichay registration succeeded but returned incomplete registration data.", HttpStatus.BAD_GATEWAY);
        }

        Map<String, Object> root = configuration.getData();

        Map<String, Object> jp = (Map<String, Object>) root.get(JANPARICHAY_TAB);

        jp.put("client_serviceId", serviceRequest.getServiceId());
        jp.put("client_id", jpResponse.getData().getClientId());
        jp.put("client_secret", jpResponse.getData().getClientSecret());
        jp.put("service_type", jpResponse.getData().getServiceType());

        log.info("JanParichay service registered successfully. ServiceId={},Response={}", serviceRequest.getServiceId(),entityToString(jpResponse));

        return configuration;
    }

    private ServiceRegistrationRequest buildRequest(Configuration configuration, Map<String, Object> basic) {

        ServiceRegistrationRequest request = new ServiceRegistrationRequest();

        String prodUrl = (String) basic.get("PROD_URL");
        String preProdUrl = (String) basic.get("PREPROD_URL");

        List<String> prodDomains = splitString(prodUrl);
        List<String> preProdDomains = splitString(preProdUrl);

        String primaryProdDomain = prodDomains.getFirst();

        request.setServiceId(configuration.getTenantId());

        request.setDisplayName((String) basic.get("tenantName"));
        request.setDescription((String) basic.get("tenantName"));

        request.setHomeUrl(buildHttpsUrl(primaryProdDomain, properties.getRedirectUri()));
        request.setLoginUrl(buildHttpsUrl(primaryProdDomain, "/login"));
        request.setLogoutUrl(buildHttpsUrl(primaryProdDomain, "/logout"));

        request.setServiceOwnerName((String) basic.get("tenantName"));

        request.setServiceState(String.valueOf(basic.get("state_name")));
        request.setServiceCategory("ServicePlus");

        request.setEnvironment(environment);
        request.setSubService("1");
        request.setLoginType((Integer) basic.get("loginType"));
        request.setEnforceMultiFactor((Integer) basic.get("enableMultiFactor"));
        request.setForceMultiFactor((Integer) basic.get("forceMultiFactor"));  //User can enable remember me option but if forceMultiFactor everytime MFA is required
        request.setIsSDL("0");
        request.setAcrValues("000000");
        request.setConsentValues("00000");
        request.setSsoType("Oauth");
        request.seteFileNo("P/1238/6783");

        List<String> alternateRedirectUris = new ArrayList<>();

        for (String domain : prodDomains) {
            alternateRedirectUris.add(buildHttpsUrl(domain, properties.getRedirectUri()));
        }

        for (String domain : preProdDomains) {
            alternateRedirectUris.add(buildHttpsUrl(domain, properties.getRedirectUri()));
        }

        request.setAlternateRedirectURIs(alternateRedirectUris.stream().distinct().collect(Collectors.toList()));

        request.setServiceOwnerName((String) basic.get("tenantSPOCName"));
        request.setServiceOwnerEmailId((String) basic.get("tenantSPOCEmailId"));
        request.setServiceOwnerMobileNo((String) basic.get("tenantSPOCContactNo"));
        request.setServiceOwnerUId((String) basic.get("tenantSPOCEmailId"));


        return request;
    }

    private String buildHttpsUrl(String domain, String path) {

        if (isEmpty(domain)) {
            return "";
        }

        String cleanDomain = domain.trim();

        if (cleanDomain.startsWith("https://") || cleanDomain.startsWith("http://")) {
            return cleanDomain.endsWith("/") ? cleanDomain.substring(0, cleanDomain.length() - 1) + normalizePath(path) : cleanDomain + normalizePath(path);
        }

        return "https://" + cleanDomain.replaceAll("/+$", "") + normalizePath(path);
    }

    private String normalizePath(String path) {

        if (isEmpty(path)) {
            return "";
        }

        return path.startsWith("/") ? path : "/" + path;
    }
}