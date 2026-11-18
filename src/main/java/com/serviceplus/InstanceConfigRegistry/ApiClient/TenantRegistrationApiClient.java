package com.serviceplus.InstanceConfigRegistry.ApiClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.serviceplus.InstanceConfigRegistry.ApiExecutor.ApiExecutor;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.dto.TenantRegistrationRequest;
import com.serviceplus.InstanceConfigRegistry.dto.TenantRegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.entityToString;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.getUserSessionDetails;

@Service
public class TenantRegistrationApiClient {

    private static final Logger Logger = LogManager.getLogger("TenantRegistrationApiClient");

    @Autowired
    private ApiExecutor<?> synchronousApiExecutor;

    @Value("${splus.usermanagement.service}")
    private String tenantRegistrationUrl;

    public TenantRegistrationResponse registerTenant(TenantRegistrationRequest request, HttpServletRequest httpRequest) throws Exception {

        String requestId = UUID.randomUUID().toString();

        try {

            Logger.info("Calling tenant registration API requestId={} tenantName={}", requestId, request.getTenantName());

            Map<String, String> headers = buildHeaders(httpRequest);
            String url = tenantRegistrationUrl.concat("/b/tenant/register");

            ResponseEntity<?> response = synchronousApiExecutor
                                                    .callExternalEndpoint(
                                                            TenantRegistrationResponse.class,
                                                            HttpMethod.POST,
                                                            headers,
                                                            new HashMap<>(),
                                                            url,
                                                            entityToString(request),
                                                            MediaType.APPLICATION_JSON
                                                    );

            if (response == null) {
                throw new SPRuntimeError("Tenant registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {
                Logger.error("Tenant registration API failed requestId={} status={}", requestId, response.getStatusCode());
                throw new SPRuntimeError("Tenant registration failed", HttpStatus.valueOf(response.getStatusCode().value()));
            }

            if (response.getBody() == null) {
                throw new SPRuntimeError("Empty tenant registration response", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Logger.info("Tenant registration API completed requestId={} tenantName={}", requestId, request.getTenantName());

            return (TenantRegistrationResponse) response.getBody();

        } catch (Exception ex) {

            Logger.error("Tenant registration API failed requestId={} tenantName={}", requestId, request.getTenantName(), ex);

            if (ex instanceof SPRuntimeError) {
                throw ex;
            }

            throw new SPRuntimeError("Unable to register tenant", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean checkUserExistInTenant(String tenantId, HttpServletRequest httpRequest) {

        String requestId = UUID.randomUUID().toString();

        try {

            Logger.info("Checking users for tenantId={}, requestId={}", tenantId, requestId);

            Map<String, String> headers = buildHeaders(httpRequest);

            String url = tenantRegistrationUrl.concat("/b/checkUserExistInTenant/").concat(tenantId);

            ResponseEntity<?> response =
                    synchronousApiExecutor.callExternalEndpoint(
                            Boolean.class,
                            HttpMethod.GET,
                            headers,
                            new HashMap<>(),
                            url,
                            null,
                            MediaType.APPLICATION_JSON
                    );

            if (response == null) {
                throw new SPRuntimeError("Unable to verify users for tenant", HttpStatus.BAD_GATEWAY);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {

                Logger.error("User existence check failed. tenantId={}, requestId={}, status={}", tenantId, requestId, response.getStatusCode());
                throw new SPRuntimeError("Unable to verify users for tenant", HttpStatus.BAD_GATEWAY);
            }

            Boolean userExists = (Boolean) response.getBody();

            if (userExists == null) {
                throw new SPRuntimeError("Empty response while checking tenant users", HttpStatus.BAD_GATEWAY);
            }

            Logger.info("User existence check completed. tenantId={}, userExists={}", tenantId, userExists);

            return userExists;

        } catch (SPRuntimeError ex) {
            throw ex;

        } catch (Exception ex) {

            Logger.error("Failed to check users for tenantId={}, requestId={}", tenantId, requestId, ex);
            throw new SPRuntimeError("Unable to verify users for tenant", HttpStatus.BAD_GATEWAY);
        }
    }

    private Map<String, String> buildHeaders(HttpServletRequest request){

        Map<String, String> headers = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        headers.put("USER-DETAILS",entityToString(getUserSessionDetails(request)));

        return headers;
    }
}
