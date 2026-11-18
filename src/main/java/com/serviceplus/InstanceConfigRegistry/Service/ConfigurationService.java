package com.serviceplus.InstanceConfigRegistry.Service;

import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.*;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.*;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.serviceplus.InstanceConfigRegistry.ApiClient.TenantRegistrationApiClient;
import com.serviceplus.InstanceConfigRegistry.Entity.*;
import com.serviceplus.InstanceConfigRegistry.Kafka.Producers.NotificationKafkaProducer;
import com.serviceplus.InstanceConfigRegistry.Kafka.Producers.TenantDomainKafkaProducer;
import com.serviceplus.InstanceConfigRegistry.Repository.*;
import com.serviceplus.InstanceConfigRegistry.SSO.Properties.SsoProperties;
import com.serviceplus.InstanceConfigRegistry.SSO.Provider.IdentityProvider;
import com.serviceplus.InstanceConfigRegistry.SSO.Provider.SSOProvider;
import com.serviceplus.InstanceConfigRegistry.SSO.Registry.IdentityProviderRegistry;
import com.serviceplus.InstanceConfigRegistry.dto.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.dto.SaveConfigRequest.Data;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ConfigurationService {

    private final ConfigurationRepository repository;

    private final TenantResolver tenantResolver;

    private final UserTenantMappingRepository userTenantMappingRepository;

    private final RedisService redis;

    private final MasterRepository masterRepository;

    private final TenantLookupRepository tenantLookupRepository;

    private final MongoClient mongoClient;

    private final ObjectMapper objectMapper;

    private final ResourceLoader resourceLoader;

    private final TenantRegistrationApiClient tenantRegistrationApiClient;

    private final UserTenantAccessRepository userTenantAccessRepository;

    private final TenantDomainKafkaProducer tenantDomainKafkaProducer;

    private final IdentityProviderRegistry identityProviderRegistry;

    private final SsoProperties ssoProperties;

    private final NotificationKafkaProducer notificationKafkaProducer;

    private static final Logger logger =  LoggerFactory.getLogger("tenantConfiguration");

    public ConfigurationService(ConfigurationRepository repository, TenantResolver tenantResolver, UserTenantMappingRepository userTenantMappingRepository, RedisService redis, MasterRepository masterRepository, TenantLookupRepository tenantLookupRepository, MongoClient mongoClient, ObjectMapper objectMapper, ResourceLoader resourceLoader, TenantRegistrationApiClient tenantRegistrationApiClient, UserTenantAccessRepository userTenantAccessRepository, TenantDomainKafkaProducer tenantDomainKafkaProducer, IdentityProviderRegistry identityProviderRegistry, SsoProperties ssoProperties, NotificationKafkaProducer notificationKafkaProducer) {
        this.repository = repository;
        this.tenantResolver = tenantResolver;
        this.userTenantMappingRepository = userTenantMappingRepository;
        this.redis = redis;
        this.masterRepository = masterRepository;
        this.tenantLookupRepository = tenantLookupRepository;
        this.mongoClient = mongoClient;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.tenantRegistrationApiClient = tenantRegistrationApiClient;
        this.userTenantAccessRepository = userTenantAccessRepository;
        this.tenantDomainKafkaProducer = tenantDomainKafkaProducer;
        this.identityProviderRegistry = identityProviderRegistry;
        this.ssoProperties = ssoProperties;
        this.notificationKafkaProducer = notificationKafkaProducer;
    }

    public Optional<Configuration> getConfiguration(String tenantId, HttpServletRequest request) {
    	
    	try {
	    	Configuration fetch = (Configuration) redis.fetch(tenantId, Configuration.class);

            logger.info("For tenantId {} redis data is present {} with data {}",tenantId,fetch != null,(fetch != null ? fetch.getData() : "NA"));
	    	
	    	if(fetch != null) {
	    		return Optional.of(fetch);
	    	}
	    	
	        Optional<Configuration> config = repository.findByTenantId(tenantId);

            logger.info("For tenantId {} mongo data is present {} with data {}",tenantId,config.isPresent(), (config.isPresent() ? config.get().getData() : "NA"));

            config.ifPresent(
                    configuration -> redis.add(configuration, tenantId)
            );

	        return config;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		throw new SPRuntimeError();
    	}
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> updateConfigValue(String tenantId, List<Data> data, HttpServletRequest request) throws Exception {

        try {

            logger.info("Config update started for tenantId={}", tenantId);

            Optional<Configuration> configOpt = repository.findById(tenantId);

            logger.info("Existing configuration found={}", configOpt.isPresent());

            String PREPROD_URL = "";
            String PROD_URL = "";
            String tenantName = "";
            String tId = "";
            boolean fromFE = false;

            Map<String, Object> rootMap;

            if (configOpt.isEmpty()) {
                logger.info("No existing config found for tenantId={}", tenantId);
                rootMap = new HashMap<>();

            } else {
                logger.info("Loading existing config for tenantId={}", tenantId);
                Configuration configuration = configOpt.get();

                Map<String, Object> existingData = configuration.getData();
                rootMap = (existingData != null) ? deepCopy(existingData) : new HashMap<>();
                Map<String, Object> basic = (Map<String, Object>) existingData.get("BASIC");

                if (basic != null) {
                    PREPROD_URL = (String) basic.get("PREPROD_URL");
                    PROD_URL = (String) basic.get("PROD_URL");
                    tenantName = (String) basic.get("tenantName");
                }

                tId = configuration.getTenantId();
            }

            Map<String, Object> master;

            logger.info("Loading defaultconfig.json");

            Resource configResource = resourceLoader.getResource("classpath:defaultconfig.json");

            try (InputStream is = configResource.getInputStream()) {
                Configuration config = objectMapper.readValue(is, Configuration.class);
                master = config.getData();

                logger.info("Master config loaded successfully");

            } catch (IOException e) {
                logger.error("Failed to load defaultconfig.json", e);
                throw new RuntimeException(e);
            }

            UserSessionObject user = requireNonNull(getUserSessionDetails(request));

            logger.info("Config update requested by userId={}, email={},user ={}", user.getUserID(), user.getEmailId(),entityToString(user));

            boolean isInstanceAdmin = user.getInstanceAdmin();

            logger.info("isInstanceAdmin={}", isInstanceAdmin);

            validateTenantAccess(tenantId,user);

            boolean newConfigRequest = DEFAULT_TENANT_ID.equals(tenantId);

            if (!newConfigRequest) {

                data.removeIf(d -> {

                    String key = d.getKey();

                    boolean restrictedUrl = key != null && (key.contains("PREPROD_URL") || key.contains("PROD_URL"));

                    if (restrictedUrl) {
                        logger.info("Ignoring restricted config update for existing tenant. tenantId={}, key={}", tenantId, key);
                    }

                    return restrictedUrl;
                });
            }

            saveNode(data, rootMap, master, isInstanceAdmin);

            for (SaveConfigRequest.Data d : data) {

                String key = d.getKey();
                Object value = d.getValue();

                logger.info("Processing config key={}", key);

                if (key.contains("PREPROD_URL")) {

                    PREPROD_URL = (String) value;

                    logger.info("Validating PREPROD_URL={}", PREPROD_URL);

                    if(newConfigRequest) {
                        verifyDuplicateDomains(PREPROD_URL, tId);
                    }

                    fromFE = true;

                } else if (key.contains("PROD_URL")) {

                    PROD_URL = (String) value;

                    logger.info("Validating PROD_URL={}", PROD_URL);

                    if(newConfigRequest) {
                        verifyDuplicateDomains(PREPROD_URL, tId);
                    }

                    fromFE = true;
                }

                if (key.contains("tenantName")) {
                    tenantName = (String) value;
                    logger.info("tenantName received={}", tenantName);
                }
            }

            if (newConfigRequest && (isEmpty(PREPROD_URL) || isEmpty(PROD_URL) || isEmpty(tenantName))) {

                logger.error("Mandatory fields missing. PREPROD_URL={}, PROD_URL={}, tenantName={}", PREPROD_URL, PROD_URL, tenantName);
                throw new SPRuntimeError("PROD_URL & PREPROD_URL & tenantName values are mandatory", HttpStatus.BAD_REQUEST);
            }

            if (newConfigRequest && fromFE) {
                validateProdPreprodDomains(PROD_URL, PREPROD_URL);
            }

            Configuration configuration;

            if (configOpt.isEmpty()) {
                logger.info("Creating new configuration entity");

                configuration = new Configuration();
                configuration.setTenantId(tenantId);

            } else {
                configuration = configOpt.get();
            }

            configuration.setData(rootMap);

            if (newConfigRequest) {
                logger.info("New tenant creation flow triggered");

                configuration.setTenantId(null);
            }
            else {

                if (!isInstanceAdmin &&  Boolean.FALSE.equals(configuration.getActive())) {
                    throw new SPRuntimeError("Tenant Configuration has been deactivated. Please contact Instance Admin.", HttpStatus.BAD_REQUEST);
                }

                configuration.setActive(Boolean.TRUE);
            }

            Long userID = user.getUserID();

            configuration = repository.save(configuration);

            logger.info("Configuration saved successfully for tenantId={}", configuration.getTenantId());

            if(newConfigRequest) {

                registerTenantDomains(configuration.getTenantId(), PROD_URL, PREPROD_URL, configuration.getActive());
                saveUserTennantMapping(configuration.getTenantId(), userID, PROD_URL + "," + PREPROD_URL, tenantName);
            }


            if (newConfigRequest) {
                try {
                    logger.info("Registering tenant with JanParichay.");

                    SSOProvider provider = ssoProperties.getProvider();

                    IdentityProvider identityProvider = identityProviderRegistry.getProvider(provider);

                    configuration = identityProvider.registerService(request, configuration);

                    if (configuration == null) {
                        throw new SPRuntimeError("Failed to register tenant with " + provider, HttpStatus.BAD_GATEWAY);
                    }

                    configuration = repository.save(configuration);

                    logger.info("Tenant registered successfully with {}.", provider);

                } catch (Exception ex) {

                    logger.error("JanParichay registration failed. Rolling back configuration.", ex);
                    deleteTenantConfiguration(configuration.getTenantId(), request);
                    throw ex;
                }
            }

            TenantRegistrationResponse response;

            if (newConfigRequest) {
                logger.info("Creating tenant admin user");

                try {

                    Map<String, Object> basic = (Map<String, Object>) configuration.getData().get(BASIC_TAB);

                    TenantRegistrationRequest req = new TenantRegistrationRequest();
                    req.setTenantId(configuration.getTenantId());

                    String adminEmail = basic.get("tenantSPOCEmailId") != null ? basic.get("tenantSPOCEmailId").toString().trim() : "";
                    String adminMobile = basic.get("tenantSPOCContactNo") != null ? basic.get("tenantSPOCContactNo").toString().trim() : "";

                    if (isEmpty(adminEmail)) {
                        throw new SPRuntimeError("Tenant Admin email is mandatory", HttpStatus.BAD_REQUEST);
                    }

                    if (isEmpty(adminMobile)) {
                        throw new SPRuntimeError("Tenant Admin mobile number is mandatory", HttpStatus.BAD_REQUEST);
                    }

                    req.setTenantId(configuration.getTenantId());
                    req.setAdminEmail(adminEmail);
                    req.setAdminMobile(adminMobile);
                    req.setTenantName(tenantName);

                    response = tenantRegistrationApiClient.registerTenant(req, request);

                    logger.info("Tenant admin created successfully");

                    UserTenantAccess access = new UserTenantAccess();

                    access.setTenantId(configuration.getTenantId());
                    access.setLoginId(response.getAdminUsername());
                    access.setActive(true);
                    access.setCreatedBy(user.getUserID());
                    access.setCreatedOn(new Date());

                    userTenantAccessRepository.save(access);

                    String correlationId = configuration.getTenantId() + "-" + UUID.randomUUID();

                    notificationKafkaProducer.sendTenantAdminUserId(configuration.getTenantId(), adminEmail, response.getAdminUsername(), correlationId);

                    notificationKafkaProducer.sendTenantAdminPassword(configuration.getTenantId(), adminMobile, response.getTemporaryPassword(), correlationId);

                    String contentSecurityPolicy = basic.get("contentSecurityPolicy") != null ? basic.get("contentSecurityPolicy").toString() : "";
                    Boolean ipWhitelistRequired = basic.get("ipwhitelist_required") != null && Boolean.parseBoolean(basic.get("ipwhitelist_required").toString());
                    List<String> ipWhitelist = splitString((String) basic.get("ip_list"));
                    List<String> ipBlacklist = splitString((String) basic.get("ip_list_restricted"));

                    tenantDomainKafkaProducer
                            .publishTenantDomainEvent(
                                    configuration.getTenantId(),
                                    PROD_URL,
                                    PREPROD_URL,
                                    ACTION_CREATE,
                                    configuration.getActive(),
                                    contentSecurityPolicy,
                                    ipWhitelistRequired,
                                    ipWhitelist,
                                    ipBlacklist
                            );

                } catch (Exception ex) {
                    logger.error("Tenant admin creation failed. Rolling back config", ex);
                    deleteTenantConfiguration(configuration.getTenantId(),request);
                    throw ex;
                }
            }

            logger.info("Clearing redis cache for tenantId={}", tenantId);

            redis.deleteKeysWithPrefix(tenantId);

            return Map.of("message", "Configuration saved successfully", "tenantId", configuration.getTenantId());

        } catch (Exception e) {

            logger.error("Configuration update failed for tenantId={}", tenantId, e);

            if (e instanceof SPRuntimeError) {
                throw e;
            }

            e.printStackTrace();

            throw new SPRuntimeError();
        }
    }

    private void registerTenantDomains(String tenantId, String prodUrls, String preprodUrls, Boolean active) {

        logger.info("Registering domains for tenantId={} prodUrls={} preprodUrls={}", tenantId, prodUrls, preprodUrls);

        registerDomains(tenantId, prodUrls, "PROD",active);
        registerDomains(tenantId, preprodUrls, "PREPROD",active);
    }

    private void registerDomains(String tenantId, String domains, String type, Boolean active) {

        for (String domain : splitString(domains)) {

            logger.info("Registering {} domain={} -> tenantId={}", type, domain, tenantId);

            tenantResolver.add(domain, tenantId,active);
        }
    }


    public Configuration getConfigValues(String tenantId, List<String> keys, HttpServletRequest request) {
        try {
         
        	Configuration conf = new Configuration();

            Optional<Configuration> config = getConfiguration(tenantId, request);

            if (config.isPresent()) {
                Map<String, Object> configData = config.get().getData();

                if (configData != null) {
                	
                	Map<String, Object> nodes = getNodesData(keys,configData);

                    logger.info("Resolved nodes for tenantId {} nodes {} ",tenantId,nodes);
                	
                	conf.setData(nodes);
                }
            }

            return conf;

        } catch (Exception e) {
            e.printStackTrace();
            throw new SPRuntimeError();
        }
    }


    public Map<String, Object> fetchFromMaster(List<String> keys) {
        try {

            List<Master> all = masterRepository.findAll();
            Map<String, Object> nodes = new HashMap<>();
            if(!all.isEmpty()) {
                Master master =  all.get(0);

                if(keys.isEmpty()){
                    nodes = master.getData();
                }
                else {
                    nodes = getNodesData(keys, master.getData());
                }
            }

            return nodes;

        } catch (Exception e) {
            e.printStackTrace();
            throw new SPRuntimeError();
        }
    }

    public Map<String, String> deleteTenantConfiguration(String tenantId, HttpServletRequest request) {

        ClientSession session = mongoClient.startSession();

        try {

            boolean userExists = tenantRegistrationApiClient.checkUserExistInTenant(tenantId, request);

            if (userExists) {
                logger.warn("Tenant configuration deletion rejected because users exist. tenantId={}", tenantId);
                throw new SPRuntimeError("Tenant cannot be deleted because users exist for this tenant", HttpStatus.BAD_REQUEST);
            }

            session.startTransaction();

            repository.deleteByTenantId(tenantId, session, Configuration.class, "_id");
            userTenantMappingRepository.deleteByTenantId(tenantId, session, UserTenantMapping.class, "tenantId");
            tenantLookupRepository.deleteByTenantId(tenantId, session, TenantLookUp.class, "tenantId");

            session.commitTransaction();

            return Map.of("message", "Configuration deleted successfully");

        } catch (SPRuntimeError e) {

            if (session.hasActiveTransaction()) {
                session.abortTransaction();
            }

            throw e;

        } catch (Exception e) {

            if (session.hasActiveTransaction()) {
                session.abortTransaction();
            }

            logger.error("Tenant configuration deletion failed. tenantId={}", tenantId, e);
            throw new SPRuntimeError();

        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
	public List<UserTenantMapping> getRegisteredTenants(HttpServletRequest request) {
		try {
			UserSessionObject user = getUserSessionDetails(request);
			Long userID = user.getUserID();

			List<UserTenantMapping> mappings = userTenantMappingRepository.findByUserId(userID);

            if(mappings == null || mappings.isEmpty()) {
            	return List.of();
            }


            mappings.forEach(entry -> {
                Optional<Configuration> config = repository.findByTenantId(entry.getTenantId());
                if(config.isPresent()){
                    Map<String, Object> basic = (Map<String, Object>) config.get().getData().get(BASIC_TAB);
                    entry.setProdUrl((String) basic.get("PROD_URL"));
                    entry.setPreprodUrl((String) basic.get("PREPROD_URL"));
                    entry.setSpocName((String) basic.get("tenantSPOCName"));
                    entry.setSpocEmail((String) basic.get("tenantSPOCEmailId"));
                    entry.setSpocMobile((String) basic.get("tenantSPOCContactNo"));

                    Optional<UserTenantAccess> access = userTenantAccessRepository.findByTenantId(entry.getTenantId());

                    access.ifPresent(userTenantAccess -> entry.setTenantUserId(userTenantAccess.getLoginId()));

                }
            });

            return mappings;

        } catch (Exception e) {
            e.printStackTrace();
            throw new SPRuntimeError();
        }
	}

	private void saveUserTennantMapping(String tId,Long userID,String domain,String tenantName) {
		UserTenantMapping userTenantMapping = new UserTenantMapping();
        userTenantMapping.setTenantId(tId);
        userTenantMapping.setUserId(userID);
        userTenantMapping.setTenant(domain);
        userTenantMapping.setTenantName(tenantName);
        userTenantMappingRepository.save(userTenantMapping);
	}

    private void verifyDuplicateDomains(String domains, String tenantId) {

        for (String domain : splitString(domains)) {

            logger.info("Checking duplicate domain={} for tenantId={}", domain, tenantId);
            verifyDuplicateDomain(domain, tenantId);
        }
    }

	private void verifyDuplicateDomain(String domain, String tId) {
		
		if(!isEmpty(domain)) {
			Optional<TenantLookUp> tenant = tenantResolver.findTenant(domain);
	    	if(tenant.isPresent() && !tenant.get().getTenantId().equals(tId)) {
	    		throw new SPRuntimeError("Tenant with the provided URL ("+ domain +") already exists. Please try again with a unique URL", HttpStatus.CONFLICT);
	    	}
    	}
		
	}

    public Map<String, String> updateMaster(SaveConfigRequest dto, HttpServletRequest request) {
        try {
            List<Master> all = masterRepository.findAll();
            Master masterFromFile = all.getFirst();
            masterFromFile.setId(1);
            Map<String, Object> rootMap = masterFromFile.getData();

            saveNode(dto.getData(), rootMap,rootMap,false);
            filterDuplicatesInRootMap(rootMap);

            masterFromFile.setData(rootMap);

            masterRepository.save(masterFromFile);

            return Map.of("message", "success");
        } catch (Exception e) {
            e.printStackTrace();
            throw new SPRuntimeError();
        }
    }


    public List<TenantDTO> getAllTenant() {
        try {
            List<UserTenantMapping> tenantList = userTenantMappingRepository.findAll();

            return tenantList.stream()
                    .map(tenant -> {
                        TenantDTO tenantDTO = new TenantDTO();
                        tenantDTO.setTenantName(tenant.getTenantName());
                        String domains = tenant.getTenant();
                        if (domains != null && !domains.trim().isEmpty()) {
                        	String domain = domains.split(",")[1];
                            tenantDTO.setDomain(domain);
                        }
                        return tenantDTO;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new SPRuntimeError("Failed to fetch tenant details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<TenantResponse> getMyTenants(HttpServletRequest request) {

        UserSessionObject user = getUserSessionDetails(request);

        logger.info("Fetching tenant list for userId={}", user.getUserID());

        List<UserTenantAccess> mappings = userTenantAccessRepository.findByLoginIdAndActiveTrue(user.getSignNo());

        if (mappings.isEmpty()) {
            logger.info("No tenant access found for userId={}", user.getUserID());
            return Collections.emptyList();
        }

        List<String> tenantIds = mappings.stream().map(UserTenantAccess::getTenantId).toList();

        logger.info("Tenant ids={}", tenantIds);
        List<Configuration> configs = repository.findAllById(tenantIds);

        List<TenantResponse> responseList = getTenantResponses(configs);

        logger.info("Total tenants fetched={}", responseList.size());

        return responseList;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private static List<TenantResponse> getTenantResponses(List<Configuration> configs) {
        List<TenantResponse> responseList = new ArrayList<>();

        for (Configuration config : configs) {

            Map<String, Object> data = config.getData();

            Map<String, Object> basic = data != null ? (Map<String, Object>) data.get("BASIC") : null;

            TenantResponse response = new TenantResponse();
            response.setTenantId(config.getTenantId());
            response.setTenantName(basic != null ? (String) basic.get("tenantName") : "");
            response.setProdUrl(basic != null ? (String) basic.get("PROD_URL") : "");
            response.setPreprodUrl(basic != null ? (String) basic.get("PREPROD_URL") : "");

            responseList.add(response);
        }
        return responseList;
    }

    private void validateTenantAccess(String tenantId, UserSessionObject user) {

        boolean isTenantAdmin = !user.getInstanceAdmin();

        if (!isTenantAdmin) {
            logger.info("Instance admin detected. Skipping tenant access validation");
            return;
        }

        logger.info("Validating tenant access for loginId={} and tenantId={}", user.getSignNo(), tenantId);

        Optional<UserTenantAccess> accessOpt = userTenantAccessRepository.findByTenantIdAndLoginIdAndActiveTrue(tenantId, user.getSignNo());

        if (accessOpt.isEmpty()) {
            logger.error("Unauthorized tenant access. loginId={}, tenantId={}", user.getSignNo(), tenantId);
            throw new SPRuntimeError("Unauthorized tenant access", HttpStatus.FORBIDDEN);
        }

        logger.info("Tenant access validation successful for tenantId={}", tenantId);
    }

    private void validateProdPreprodDomains(String prodUrls, String preprodUrls) {

        Set<String> prodDomains = new HashSet<>(splitString(prodUrls));
        Set<String> preprodDomains = new HashSet<>(splitString(preprodUrls));

        prodDomains.retainAll(preprodDomains);

        if (!prodDomains.isEmpty()) {

            logger.error("Domains configured in both PROD and PREPROD: {}", prodDomains);
            throw new SPRuntimeError("PROD_URL & PREPROD_URL must contain different domains", HttpStatus.BAD_REQUEST);
        }
    }
}
