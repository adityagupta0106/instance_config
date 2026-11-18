package com.serviceplus.InstanceConfigRegistry.Service;

import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarApiConfiguration;
import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarConfiguration;
import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarNodeConfiguration;
import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarNodeValueConfiguration;
import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarPayloadConfiguration;
import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarPluginMethodMaster;
import com.serviceplus.InstanceConfigRegistry.Entity.TransactionIdConfiguration;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.Repository.AadhaarConfigurationRepository;
import com.serviceplus.InstanceConfigRegistry.Repository.AadhaarPluginMethodMasterRepository;
import com.serviceplus.InstanceConfigRegistry.dto.*;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarApiType;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarConfigurationMode;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarMappingType;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarSystemGeneratedType;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.serviceplus.InstanceConfigRegistry.enums.RequestResponseType.REQUEST;
import static com.serviceplus.InstanceConfigRegistry.enums.RequestResponseType.RESPONSE;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.isEmpty;

@Service
public class AadhaarConfigurationService {

    private static final Logger log = LoggerFactory.getLogger("tenantConfiguration");

    private final AadhaarConfigurationRepository aadhaarConfigurationRepository;
    private final AadhaarPluginMethodMasterRepository aadhaarPluginMethodMasterRepository;

    public AadhaarConfigurationService(AadhaarConfigurationRepository aadhaarConfigurationRepository, AadhaarPluginMethodMasterRepository aadhaarPluginMethodMasterRepository) {
        this.aadhaarConfigurationRepository = aadhaarConfigurationRepository;
        this.aadhaarPluginMethodMasterRepository = aadhaarPluginMethodMasterRepository;
    }

    public AadhaarConfigurationResponse save(AadhaarConfigurationRequest request, UserSessionObject user) {

        boolean update = request.getId() != null && !request.getId().isBlank();

        log.info("Saving Aadhaar configuration. configurationId={}, operation={}, configurationName={}, auaName={}, userId={}", request.getId(),
                update ? "UPDATE" : "CREATE", request.getConfigurationName(), request.getAuaName(), user.getUserID());

        validateConfigurationPayload(request);

        AadhaarConfiguration configuration;

        if (update) {

            configuration = getConfigurationOrThrow(request.getId());
            log.info("Existing Aadhaar configuration found. configurationId={}, version={}", configuration.getId(), configuration.getVersion());

        } else {

            configuration = new AadhaarConfiguration();

            LocalDateTime now = LocalDateTime.now();

            configuration.setCrDate(now);
            configuration.setCrBy(user.getUserID());
            configuration.setVersion(1);

            log.info("Preparing new Aadhaar configuration. createdBy={}", user.getUserID());
        }

        mapRequestToEntity(configuration, request);
        configuration.setUpDate(LocalDateTime.now());
        configuration.setUpBy(user.getUserID());

        if (update) {

            Integer version = configuration.getVersion();
            configuration.setVersion(version == null ? 1 : version + 1);
        }

        AadhaarConfiguration saved = aadhaarConfigurationRepository.save(configuration);

        log.info("Aadhaar configuration saved successfully. configurationId={}, operation={}, version={}, updatedBy={}",
                saved.getId(), update ? "UPDATE" : "CREATE",
                saved.getVersion(),
                user.getUserID());

        return mapEntityToResponse(saved);
    }

    private void validateConfigurationPayload(AadhaarConfigurationRequest request) {

        if (request == null) {
            log.error("Aadhaar configuration payload is null");
            throw new SPRuntimeError("Aadhaar configuration payload is required", HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(request.getAuaName())) {
            log.error("AUA name is missing in Aadhaar configuration payload");
            throw new SPRuntimeError("AUA name is required", HttpStatus.BAD_REQUEST);
        }

        validateEnabledModes(request);

        validateTransactionIdConfiguration(request.getTransactionIdConfiguration());

        validateApiConfigurations(request.getApiConfigurations());

        log.info("Aadhaar configuration payload is valid. configurationName={}, authenticationType={}, apiCount={}", request.getConfigurationName(), request.getAuthenticationType(), request.getApiConfigurations() != null ? request.getApiConfigurations().size() : 0);
    }

    private void validateEnabledModes(AadhaarConfigurationRequest request) {

        if (request.getAuthenticationType() == null) {
            return;
        }

        if (request.getEnabledModes() == null || request.getEnabledModes().isEmpty()) {

            log.error("Enabled modes are missing. authenticationType={}, configurationName={}", request.getAuthenticationType(), request.getConfigurationName());
            throw new SPRuntimeError("At least one Aadhaar authentication mode is required", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateTransactionIdConfiguration(TransactionIdConfiguration request) {

        if (request == null) {

            log.error("Transaction ID configuration is missing");
            throw new SPRuntimeError("Transaction ID configuration is required", HttpStatus.BAD_REQUEST);
        }

        log.info("Validating transaction ID configuration");
    }

    private void validateApiConfigurations(List<AadhaarApiConfigurationRequest> apiConfigurations) {

        if (apiConfigurations == null || apiConfigurations.isEmpty()) {

            log.error("No API configurations provided");

            throw new SPRuntimeError("At least one Aadhaar API configuration is required", HttpStatus.BAD_REQUEST);
        }

        Set<AadhaarApiType> configuredApiTypes = new HashSet<>();

        for (AadhaarApiConfigurationRequest api : apiConfigurations) {

            validateApiConfiguration(api);

            if (!configuredApiTypes.add(api.getApiType())) {

                log.error("Duplicate Aadhaar API configuration found. apiType={}", api.getApiType());

                throw new SPRuntimeError("Duplicate API configuration for " + api.getApiType(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void validateApiConfiguration(AadhaarApiConfigurationRequest api) {

        if (api == null) {

            log.error("API configuration contains null entry");

            throw new SPRuntimeError("API configuration cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (api.getApiType() == null) {

            log.error("API type is missing");

            throw new SPRuntimeError("API type is required", HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(api.getDisplayName())) {

            log.error("API display name is missing. apiType={}", api.getApiType());

            throw new SPRuntimeError("API display name is required for " + api.getApiType(), HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(api.getEndpoint())) {

            log.error("API endpoint is missing. apiType={}", api.getApiType());

            throw new SPRuntimeError("API endpoint is required for " + api.getApiType(), HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(api.getXmlNamespace())) {

            log.error("XML namespace is missing. apiType={}", api.getApiType());

            throw new SPRuntimeError("XML namespace is required for " + api.getApiType(), HttpStatus.BAD_REQUEST);
        }

        validatePayload(api.getRequestPayload(), api.getApiType(), REQUEST.name());

        validatePayload(api.getResponsePayload(), api.getApiType(), RESPONSE.name());
    }

    private void validatePayload(AadhaarPayloadConfigurationRequest payload, AadhaarApiType apiType, String payloadType) {

        if (payload == null) {

            log.error("{} payload is missing. apiType={}", payloadType, apiType);

            throw new SPRuntimeError(payloadType + " payload is required for " + apiType, HttpStatus.BAD_REQUEST);
        }

        if (payload.getNodes() == null || payload.getNodes().isEmpty()) {

            log.error("{} payload nodes are missing. apiType={}", payloadType, apiType);

            throw new SPRuntimeError(payloadType + " payload must contain at least one node for " + apiType, HttpStatus.BAD_REQUEST);
        }

        Set<String> nodeCodes = new HashSet<>();
        Set<String> xpaths = new HashSet<>();

        for (AadhaarNodeConfigurationRequest node : payload.getNodes()) {

            validateNodeConfiguration(node, apiType, payloadType);

            if (!nodeCodes.add(node.getNodeCode())) {

                log.error("Duplicate nodeCode found. nodeCode={}, apiType={}, payloadType={}", node.getNodeCode(), apiType, payloadType);

                throw new SPRuntimeError("Duplicate node code: " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
            }

            if (!xpaths.add(node.getXpath())) {

                log.error("Duplicate XPath found. xpath={}, apiType={}, payloadType={}", node.getXpath(), apiType, payloadType);

                throw new SPRuntimeError("Duplicate XPath: " + node.getXpath(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void validateNodeConfiguration(AadhaarNodeConfigurationRequest node, AadhaarApiType apiType, String payloadType) {

        if (node == null) {

            throw new SPRuntimeError("Node configuration cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(node.getNodeCode())) {

            log.error("Node code is missing. apiType={}, payloadType={}", apiType, payloadType);

            throw new SPRuntimeError("Node code is required", HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(node.getDisplayName())) {

            throw new SPRuntimeError("Display name is required for node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(node.getXpath())) {

            log.error("XPath is missing. nodeCode={}, apiType={}, payloadType={}", node.getNodeCode(), apiType, payloadType);

            throw new SPRuntimeError("XPath is required for node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
        }

        if (node.getMappingType() == null) {

            throw new SPRuntimeError("Mapping type is required for node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
        }

        validateNodeValueConfiguration(node);
    }


    private void validateNodeValueConfiguration(AadhaarNodeConfigurationRequest node) {

        AadhaarMappingType mappingType = node.getMappingType();

        AadhaarNodeValueConfigurationRequest valueConfiguration = node.getValueConfiguration();

        switch (mappingType) {

            case DYNAMIC:

                if (valueConfiguration != null && !isEmpty(valueConfiguration.getValue())) {
                    throw new SPRuntimeError("Value configuration is not required for dynamic node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
                }

                break;

            case STATIC:

                if (valueConfiguration != null && !isEmpty(valueConfiguration.getValue())) {
                    throw new SPRuntimeError("Static value is not required for node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
                }

                break;

            case SYSTEM_GENERATED:

                if (valueConfiguration == null || isEmpty(valueConfiguration.getSystemVariable())) {
                    throw new SPRuntimeError("System generated variable is required for node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
                }

                break;

            case PLUGIN:

                if (valueConfiguration == null || isEmpty(valueConfiguration.getPluginMethodCode())) {
                    throw new SPRuntimeError("Plugin method is required for node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
                }

                break;

            case RESULT:

                if (valueConfiguration == null || isEmpty(valueConfiguration.getValue())) {
                    throw new SPRuntimeError("Value is required for node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
                }

                break;

            case NONE,INFO,ERROR,ADD_VALUE:
                break;

            default:

                throw new SPRuntimeError("Unsupported mapping type for node " + node.getNodeCode(), HttpStatus.BAD_REQUEST);
        }
    }

    public AadhaarConfigurationResponse getById(String configurationId, UserSessionObject user) {

        log.info("Fetching Aadhaar configuration. configurationId={}, userId={}", configurationId, user.getUserID());
        AadhaarConfiguration configuration = getConfigurationOrThrow(configurationId);
        return mapEntityToResponse(configuration);
    }

    public List<AUAListing> getAll(UserSessionObject user) {

        log.info("Fetching all Aadhaar configurations. userId={}", user.getUserID());
        List<AadhaarConfiguration> configurations = aadhaarConfigurationRepository.findAll();

        if (configurations.isEmpty()) {

            log.info("No Aadhaar configurations found. userId={}", user.getUserID());
            return Collections.emptyList();
        }

        log.info("Aadhaar configurations fetched successfully. count={}, userId={}", configurations.size(), user.getUserID());
        return configurations.stream().map(this::mapForListing).collect(Collectors.toList());
    }

    private AUAListing mapForListing(AadhaarConfiguration configuration){

        AUAListing auaListing = new AUAListing();
        auaListing.setAuaName(configuration.getAuaName());
        auaListing.setId(configuration.getId());
        auaListing.setEnabledModes(configuration.getEnabledModes());
        auaListing.setLastUpdated(configuration.getUpDate());

        return auaListing;
    }

    public void delete(String configurationId, UserSessionObject user) {

        log.info("Deleting Aadhaar configuration. configurationId={}, userId={}", configurationId, user.getUserID());

        AadhaarConfiguration configuration = getConfigurationOrThrow(configurationId);
        aadhaarConfigurationRepository.delete(configuration);

        log.info("Aadhaar configuration deleted successfully. configurationId={}, userId={}", configurationId, user.getUserID());
    }

    public AadhaarConfigurationResponse updateNodeConfiguration(String configurationId, String nodeCode, AadhaarNodeConfigurationRequest request, UserSessionObject user) {

        log.info("Updating Aadhaar node configuration. configurationId={}, nodeCode={}, mappingType={}, userId={}", configurationId, nodeCode, request.getMappingType(), user.getUserID());

        AadhaarConfiguration configuration = getConfigurationOrThrow(configurationId);

        AadhaarNodeConfiguration node = findNode(configuration, nodeCode);

        updateNodeEntity(node, request);

        configuration.setUpDate(LocalDateTime.now());
        configuration.setUpBy(user.getUserID());

        Integer version = configuration.getVersion();

        if (version == null) {
            configuration.setVersion(1);
        } else {
            configuration.setVersion(version + 1);
        }

        AadhaarConfiguration saved = aadhaarConfigurationRepository.save(configuration);

        log.info("Aadhaar node configuration updated successfully. configurationId={}, nodeCode={}, version={}", configurationId, nodeCode, saved.getVersion());

        return mapEntityToResponse(saved);
    }

    public AadhaarMappingMasterResponse getAadhaarMappingMasters(UserSessionObject user) {

        log.info("Fetching Aadhaar mapping masters. userId={}", user.getUserID());

        AadhaarMappingMasterResponse response = new AadhaarMappingMasterResponse();

        List<MasterOptionResponse> pluginMethods =
                aadhaarPluginMethodMasterRepository.findAll()
                        .stream()
                        .map(entity -> new MasterOptionResponse(
                                entity.getCode(),
                                entity.getName(),
                                entity.getDescription()
                        ))
                        .collect(Collectors.toList());

        List<MasterOptionResponse> systemGeneratedFields =
                Arrays.stream(AadhaarSystemGeneratedType.values())
                        .map(field -> new MasterOptionResponse(
                                field.name(),
                                createLabel(field.name()),
                                field.getDescription()
                        ))
                        .collect(Collectors.toList());

        List<MasterOptionResponse> configurationModes =
                Arrays.stream(AadhaarConfigurationMode.values())
                        .map(mode -> new MasterOptionResponse(
                                mode.name(),
                                createLabel(mode.name()),
                                null
                        ))
                        .collect(Collectors.toList());

        List<MasterOptionResponse> mappingType =
                Arrays.stream(AadhaarMappingType.values())
                        .map(field -> new MasterOptionResponse(
                                field.name(),
                                createLabel(field.name())
                        ))
                        .toList();

        response.setPluginMethods(pluginMethods);
        response.setSystemGeneratedFields(systemGeneratedFields);
        response.setConfigurationModes(configurationModes);
        response.setMappingTypes(mappingType);

        log.info("Aadhaar mapping masters fetched. pluginMethods={}, systemGeneratedFields={}, configurationModes={}",
                pluginMethods.size(),
                systemGeneratedFields.size(),
                configurationModes.size());

        return response;
    }

    private String createLabel(String value) {

        return Arrays.stream(value.split("_"))
                .map(word ->
                        word.substring(0, 1).toUpperCase()
                                + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private void mapRequestToEntity(AadhaarConfiguration configuration, AadhaarConfigurationRequest request) {

        log.info("Mapping Aadhaar configuration request to entity. configurationName={}", request.getConfigurationName());

        configuration.setAuaName(request.getAuaName());
        configuration.setConfigurationName(request.getConfigurationName());
        configuration.setAuthenticationType(request.getAuthenticationType());
        configuration.setEnabledModes(request.getEnabledModes());
        configuration.setTransactionIdConfiguration(mapTransactionIdRequestToEntity(request.getTransactionIdConfiguration()));
        configuration.setApiConfigurations(mapApiRequestsToEntities(request.getApiConfigurations()));
    }

    private TransactionIdConfiguration mapTransactionIdRequestToEntity(TransactionIdConfiguration request) {

        if (request == null) {

            log.info("Transaction ID configuration is null");
            return null;
        }

        TransactionIdConfiguration configuration = new TransactionIdConfiguration();

        configuration.setPrefix(request.getPrefix());
        configuration.setSuffix(request.getSuffix());

        return configuration;
    }

    private TransactionIdConfigurationResponse mapTransactionIdEntityToResponse(TransactionIdConfiguration entity) {

        if (entity == null) {
            return null;
        }

        TransactionIdConfigurationResponse response = new TransactionIdConfigurationResponse();

        response.setPrefix(entity.getPrefix());

        response.setSuffix(entity.getSuffix());

        return response;
    }

    private List<AadhaarApiConfiguration> mapApiRequestsToEntities(List<AadhaarApiConfigurationRequest> requests) {

        if (requests == null || requests.isEmpty()) {

            log.info("No Aadhaar API configurations received");

            return new ArrayList<>();
        }

        List<AadhaarApiConfiguration> entities = new ArrayList<>();

        for (AadhaarApiConfigurationRequest request : requests) {

            AadhaarApiConfiguration entity = new AadhaarApiConfiguration();

            entity.setApiType(request.getApiType());
            entity.setDisplayName(request.getDisplayName());
            entity.setEndpoint(request.getEndpoint());
            entity.setXmlNamespace(request.getXmlNamespace());
            entity.setRequestPayload(mapPayloadRequestToEntity(request.getRequestPayload()));
            entity.setResponsePayload(mapPayloadRequestToEntity(request.getResponsePayload()));

            entities.add(entity);

            log.info("Mapped Aadhaar API configuration. apiType={}, displayName={}", request.getApiType(), request.getDisplayName());
        }

        return entities;
    }

    private List<AadhaarApiConfigurationResponse> mapApiEntitiesToResponses(List<AadhaarApiConfiguration> entities) {

        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        List<AadhaarApiConfigurationResponse> responses = new ArrayList<>();

        for (AadhaarApiConfiguration entity : entities) {

            AadhaarApiConfigurationResponse response = new AadhaarApiConfigurationResponse();

            response.setApiType(entity.getApiType());

            response.setDisplayName(entity.getDisplayName());

            response.setEndpoint(entity.getEndpoint());

            response.setXmlNamespace(entity.getXmlNamespace());

            response.setRequestPayload(mapPayloadEntityToResponse(entity.getRequestPayload()));

            response.setResponsePayload(mapPayloadEntityToResponse(entity.getResponsePayload()));

            responses.add(response);
        }

        return responses;
    }

    private AadhaarPayloadConfiguration mapPayloadRequestToEntity(AadhaarPayloadConfigurationRequest request) {

        if (request == null) {
            return null;
        }

        AadhaarPayloadConfiguration entity = new AadhaarPayloadConfiguration();

        entity.setXsd(request.getXsd());
        entity.setSchemaName(request.getSchemaName());
        entity.setNodes(mapNodeRequestsToEntities(request.getNodes()));

        return entity;
    }

    private AadhaarPayloadConfigurationResponse mapPayloadEntityToResponse(AadhaarPayloadConfiguration entity) {

        if (entity == null) {
            return null;
        }

        AadhaarPayloadConfigurationResponse response = new AadhaarPayloadConfigurationResponse();

        response.setXsd(entity.getXsd());

        response.setSchemaName(entity.getSchemaName());

        response.setNodes(mapNodeEntitiesToResponses(entity.getNodes()));

        return response;
    }

    private List<AadhaarNodeConfiguration> mapNodeRequestsToEntities(List<AadhaarNodeConfigurationRequest> requests) {

        if (requests == null || requests.isEmpty()) {

            log.info("No Aadhaar node configurations received");
            return new ArrayList<>();
        }

        List<AadhaarNodeConfiguration> entities = new ArrayList<>();

        for (AadhaarNodeConfigurationRequest request : requests) {

            AadhaarNodeConfiguration entity = new AadhaarNodeConfiguration();
            updateNodeEntity(entity, request);
            entities.add(entity);
        }

        return entities;
    }

    private List<AadhaarNodeConfigurationResponse> mapNodeEntitiesToResponses(List<AadhaarNodeConfiguration> entities) {

        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        List<AadhaarNodeConfigurationResponse> responses = new ArrayList<>();

        for (AadhaarNodeConfiguration entity : entities) {

            AadhaarNodeConfigurationResponse response = new AadhaarNodeConfigurationResponse();

            response.setNodeCode(entity.getNodeCode());
            response.setDisplayName(entity.getDisplayName());
            response.setXpath(entity.getXpath());
            response.setEnableLogs(entity.isEnableLogs());
            response.setMaskValue(entity.isMaskValue());
            response.setMappingType(entity.getMappingType());
            
            response.setValueConfiguration(mapNodeValueEntityToResponse(entity.getValueConfiguration()));

            responses.add(response);
        }

        return responses;
    }

    private void updateNodeEntity(AadhaarNodeConfiguration node, AadhaarNodeConfigurationRequest request) {

        log.info("Updating Aadhaar node entity. nodeCode={}, mappingType={}", request.getNodeCode(), request.getMappingType());

        node.setNodeCode(request.getNodeCode());
        node.setDisplayName(request.getDisplayName());
        node.setXpath(request.getXpath());
        node.setEnableLogs(request.isEnableLogs());
        node.setMaskValue(request.isMaskValue());
        node.setMappingType(request.getMappingType());
        node.setValueConfiguration(mapNodeValueRequestToEntity(request.getValueConfiguration(), request.getMappingType()));
    }

    private AadhaarNodeValueConfiguration mapNodeValueRequestToEntity(AadhaarNodeValueConfigurationRequest request, AadhaarMappingType mappingType) {

        if (request == null) {
            log.info("Node value configuration is null. mappingType={}", mappingType);
            return null;
        }

        AadhaarNodeValueConfiguration configuration = new AadhaarNodeValueConfiguration();

        configuration.setValue(request.getValue());
        configuration.setSystemVariable(request.getSystemVariable());
        configuration.setPluginMethodCode(request.getPluginMethodCode());
        
        boolean visibleToServiceDefiner = resolveVisibility(mappingType);

        configuration.setVisibleToServiceDefiner(visibleToServiceDefiner);
        log.info("Mapped Aadhaar node value configuration. mappingType={}, visibleToServiceDefiner={}, systemVariablePresent={}, pluginMethodPresent={}", mappingType, visibleToServiceDefiner, request.getSystemVariable() != null, request.getPluginMethodCode() != null);

        return configuration;
    }

    private boolean resolveVisibility(AadhaarMappingType mappingType) {

        if (mappingType == null) {
            return Boolean.TRUE;
        }

        return switch (mappingType) {

            case SYSTEM_GENERATED, ADD_VALUE -> Boolean.FALSE;

            default -> Boolean.TRUE;
        };
    }

    private AadhaarNodeValueConfigurationResponse mapNodeValueEntityToResponse(AadhaarNodeValueConfiguration entity) {

        if (entity == null) {
            return null;
        }

        AadhaarNodeValueConfigurationResponse response = new AadhaarNodeValueConfigurationResponse();

        response.setValue(entity.getValue());
        response.setSystemVariable(entity.getSystemVariable());
        response.setPluginMethodCode(entity.getPluginMethodCode());
        response.setVisibleToServiceDefiner(entity.isVisibleToServiceDefiner());

        return response;
    }

    private AadhaarNodeConfiguration findNode(AadhaarConfiguration configuration, String nodeCode) {

        List<AadhaarApiConfiguration> apiConfigurations = configuration.getApiConfigurations();

        if (apiConfigurations == null || apiConfigurations.isEmpty()) {

            log.error("No API configuration found while searching node. configurationId={}, nodeCode={}", configuration.getId(), nodeCode);

            throw new RuntimeException("No API configuration found");
        }

        for (AadhaarApiConfiguration apiConfiguration : apiConfigurations) {

            AadhaarNodeConfiguration node = findNodeInPayload(apiConfiguration.getRequestPayload(), nodeCode);

            if (node != null) {

                log.info("Node found in request payload. configurationId={}, apiType={}, nodeCode={}", configuration.getId(), apiConfiguration.getApiType(), nodeCode);

                return node;
            }

            node = findNodeInPayload(apiConfiguration.getResponsePayload(), nodeCode);

            if (node != null) {

                log.info("Node found in response payload. configurationId={}, apiType={}, nodeCode={}", configuration.getId(), apiConfiguration.getApiType(), nodeCode);

                return node;
            }
        }

        log.error("Aadhaar node not found. configurationId={}, nodeCode={}", configuration.getId(), nodeCode);

        throw new RuntimeException("Node not found: " + nodeCode);
    }

    private AadhaarNodeConfiguration findNodeInPayload(AadhaarPayloadConfiguration payload, String nodeCode) {

        if (payload == null || payload.getNodes() == null || payload.getNodes().isEmpty()) {

            return null;
        }

        Optional<AadhaarNodeConfiguration> node = payload.getNodes().stream().filter(item -> nodeCode.equals(item.getNodeCode())).findFirst();

        return node.orElse(null);
    }

    private AadhaarConfigurationResponse mapEntityToResponse(AadhaarConfiguration entity) {

        AadhaarConfigurationResponse response = new AadhaarConfigurationResponse();

        response.setId(entity.getId());
        response.setAuaName(entity.getAuaName());
        response.setConfigurationName(entity.getConfigurationName());
        response.setAuthenticationType(entity.getAuthenticationType());
        response.setEnabledModes(entity.getEnabledModes());
        response.setTransactionIdConfiguration(mapTransactionIdEntityToResponse(entity.getTransactionIdConfiguration()));
        response.setApiConfigurations(mapApiEntitiesToResponses(entity.getApiConfigurations()));
        response.setStatus(entity.getStatus());
        response.setVersion(entity.getVersion());

        return response;
    }

    private AadhaarConfiguration getConfigurationOrThrow(String configurationId) {

        log.info("Fetching Aadhaar configuration entity. configurationId={}", configurationId);

        return aadhaarConfigurationRepository.findById(configurationId).orElseThrow(() -> {

            log.error("Aadhaar configuration not found. configurationId={}", configurationId);
            return new SPRuntimeError("Aadhaar configuration not found",HttpStatus.BAD_REQUEST);
        });
    }

    public AadhaarDefinerConfigurationResponse getDefinerConfiguration(String configurationId, UserSessionObject user) {

        log.info("Fetching Aadhaar definer configuration. configurationId={}, userId={}", configurationId, user.getUserID());

        AadhaarConfiguration configuration =
                aadhaarConfigurationRepository.findById(configurationId)
                        .orElseThrow(() ->
                                new SPRuntimeError("Aadhaar configuration not found: " + configurationId,HttpStatus.BAD_REQUEST));

        AadhaarDefinerConfigurationResponse response = new AadhaarDefinerConfigurationResponse();

        response.setConfigurationId(configuration.getId());
        response.setConfigurationName(configuration.getConfigurationName());
        response.setAuthenticationType(configuration.getAuthenticationType());

        if (configuration.getEnabledModes() != null && !configuration.getEnabledModes().isEmpty()) {
            response.setConfigurationMode(configuration.getEnabledModes());
        }

        List<AadhaarDefinerConfigurationResponse.AadhaarDefinerApiDTO> apiResponses =
                configuration.getApiConfigurations()
                        .stream()
                        .map(this::mapDefinerApi)
                        .collect(Collectors.toList());

        response.setApiConfigurations(apiResponses);

        log.info("Aadhaar definer configuration fetched. configurationId={}, apiCount={}",
                configurationId, apiResponses.size());

        return response;
    }

    private AadhaarDefinerConfigurationResponse.AadhaarDefinerApiDTO mapDefinerApi(AadhaarApiConfiguration api) {

        AadhaarDefinerConfigurationResponse.AadhaarDefinerApiDTO dto = new AadhaarDefinerConfigurationResponse.AadhaarDefinerApiDTO();

        dto.setApiType(api.getApiType());
        dto.setDisplayName(api.getDisplayName());
        dto.setRequestPayload(mapRequestPayload(api.getRequestPayload()));
        dto.setResponsePayload(mapResponsePayload(api.getResponsePayload()));

        return dto;
    }

    private AadhaarDefinerConfigurationResponse.AadhaarDefinerPayloadDTO mapRequestPayload(AadhaarPayloadConfiguration payload) {

        if (payload == null) {
            return null;
        }

        AadhaarDefinerConfigurationResponse.AadhaarDefinerPayloadDTO dto = new AadhaarDefinerConfigurationResponse.AadhaarDefinerPayloadDTO();
        dto.setSchemaName(payload.getSchemaName());

        List<AadhaarDefinerConfigurationResponse.AadhaarDefinerNodeDTO> nodes =
                payload.getNodes()
                        .stream()
                        .filter(node ->
                                node.getMappingType() == AadhaarMappingType.DYNAMIC
                                        || node.getMappingType() == AadhaarMappingType.STATIC)
                        .map(this::mapDefinerNode)
                        .collect(Collectors.toList());

        dto.setNodes(nodes);
        return dto;
    }

    private AadhaarDefinerConfigurationResponse.AadhaarDefinerNodeDTO mapDefinerNode(AadhaarNodeConfiguration node) {

        AadhaarDefinerConfigurationResponse.AadhaarDefinerNodeDTO dto = new AadhaarDefinerConfigurationResponse.AadhaarDefinerNodeDTO();

        dto.setCode(node.getNodeCode());
        dto.setName(node.getDisplayName());
        dto.setXpath(node.getXpath());
        dto.setMappingType(node.getMappingType());

        return dto;
    }

    private AadhaarDefinerConfigurationResponse.AadhaarDefinerPayloadDTO mapResponsePayload(AadhaarPayloadConfiguration payload) {

        if (payload == null) {
            return null;
        }

        AadhaarDefinerConfigurationResponse.AadhaarDefinerPayloadDTO dto = new AadhaarDefinerConfigurationResponse.AadhaarDefinerPayloadDTO();
        dto.setSchemaName(payload.getSchemaName());

        List<AadhaarDefinerConfigurationResponse.AadhaarDefinerNodeDTO> nodes =
                payload.getNodes()
                        .stream()
                        .filter(node ->
                                node.getMappingType() == AadhaarMappingType.DYNAMIC
                                        || node.getMappingType() == AadhaarMappingType.STATIC)
                        .map(this::mapDefinerNode)
                        .collect(Collectors.toList());

        dto.setNodes(nodes);
        return dto;
    }

    public List<MasterOptionResponse> getAuaConfigurations(UserSessionObject user) {

        log.info("Fetching Aadhaar configurations for Service Definer. userId={}", user.getUserID());

        List<MasterOptionResponse> configurations =
                aadhaarConfigurationRepository.findAll()
                        .stream()
                        .map(configuration ->
                                new MasterOptionResponse(
                                        configuration.getId(),
                                        configuration.getAuaName()
                                ))
                        .collect(Collectors.toList());

        log.info("Aadhaar configurations fetched for Service Definer. count={}", configurations.size());

        return configurations;
    }
}