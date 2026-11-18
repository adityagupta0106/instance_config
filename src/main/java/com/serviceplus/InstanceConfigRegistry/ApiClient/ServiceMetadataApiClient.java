package com.serviceplus.InstanceConfigRegistry.ApiClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.InstanceConfigRegistry.ApiExecutor.ApiExecutor;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.centralrepo.dto.AvailableServicePullDTO;
import com.serviceplus.InstanceConfigRegistry.centralrepo.dto.ServicePullInfoDTO;
import com.serviceplus.InstanceConfigRegistry.dto.ServerSidePaginationRecord;
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
import java.util.List;
import java.util.Map;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.entityToString;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.getUserSessionDetails;

@Service
public class ServiceMetadataApiClient {

    private static final Logger log = LogManager.getLogger(ServiceMetadataApiClient.class);

    @Autowired
    private ApiExecutor<?> synchronousApiExecutor;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${splus.servicemetadata.service}")
    private String serviceMetadataUrl;

    public ServicePullInfoDTO getServicePullInfo(Integer serviceId, HttpServletRequest httpRequest) {
        log.info("Fetching service pull info from ServiceMetadata for serviceId={}", serviceId);
        try {
            Map<String, String> headers = buildHeaders(httpRequest);
            String url = serviceMetadataUrl + "/a/copy-service/pull-info?serviceId=" + serviceId;

            ResponseEntity<?> response = synchronousApiExecutor.callExternalEndpoint(
                    ServicePullInfoDTO.class,
                    HttpMethod.GET,
                    headers,
                    new HashMap<>(),
                    url,
                    null,
                    MediaType.APPLICATION_JSON
            );

            if (response == null || !response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new SPRuntimeError("Failed to fetch service info for serviceId: " + serviceId, HttpStatus.BAD_GATEWAY);
            }

            if (response.getBody() instanceof ServicePullInfoDTO) {
                return (ServicePullInfoDTO) response.getBody();
            }
            return objectMapper.convertValue(response.getBody(), ServicePullInfoDTO.class);
        } catch (SPRuntimeError e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling ServiceMetadata pull-info API: ", e);
            throw new SPRuntimeError("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServerSidePaginationRecord<AvailableServicePullDTO> getAvailableServicesToPull(
            String search, int page, int size, int minApplications, HttpServletRequest httpRequest) {
        log.info("Querying available services to pull from ServiceMetadata. search={}, page={}, size={}", search, page, size);
        try {
            Map<String, String> headers = buildHeaders(httpRequest);
            StringBuilder sb = new StringBuilder(serviceMetadataUrl);
            sb.append("/a/copy-service/available-to-pull?page=").append(page)
              .append("&size=").append(size)
              .append("&minApplications=").append(minApplications);

            if (search != null && !search.isBlank()) {
                sb.append("&search=").append(java.net.URLEncoder.encode(search.trim(), java.nio.charset.StandardCharsets.UTF_8));
            }

            ResponseEntity<?> response = synchronousApiExecutor.callExternalEndpoint(
                    ServerSidePaginationRecord.class,
                    HttpMethod.GET,
                    headers,
                    new HashMap<>(),
                    sb.toString(),
                    null,
                    MediaType.APPLICATION_JSON
            );

            if (response == null || !response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new SPRuntimeError("Failed to fetch available services from ServiceMetadata", HttpStatus.BAD_GATEWAY);
            }

            Object body = response.getBody();
            ServerSidePaginationRecord<?> rawRecord;
            if (body instanceof ServerSidePaginationRecord) {
                rawRecord = (ServerSidePaginationRecord<?>) body;
            } else {
                rawRecord = objectMapper.convertValue(body, ServerSidePaginationRecord.class);
            }

            List<AvailableServicePullDTO> dtoList = new java.util.ArrayList<>();
            if (rawRecord != null && rawRecord.getData() != null) {
                for (Object item : rawRecord.getData()) {
                    if (item instanceof AvailableServicePullDTO) {
                        dtoList.add((AvailableServicePullDTO) item);
                    } else {
                        dtoList.add(objectMapper.convertValue(item, AvailableServicePullDTO.class));
                    }
                }
            }

            return new ServerSidePaginationRecord<>(
                    dtoList,
                    rawRecord != null ? rawRecord.getTotalRecords() : 0,
                    rawRecord != null ? rawRecord.getCurrentPage() : page,
                    rawRecord != null ? rawRecord.getPageSize() : size,
                    rawRecord != null ? rawRecord.getTotalPages() : 0
            );
        } catch (SPRuntimeError e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling ServiceMetadata available-to-pull API: ", e);
            throw new SPRuntimeError("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, String> buildHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        if (request != null) {
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    headers.put(headerName, request.getHeader(headerName));
                }
            }
            if (getUserSessionDetails(request) != null) {
                headers.put("USER-DETAILS", entityToString(getUserSessionDetails(request)));
            }
        }
        return headers;
    }
}
