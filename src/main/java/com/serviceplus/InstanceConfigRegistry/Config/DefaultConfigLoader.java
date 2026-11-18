package com.serviceplus.InstanceConfigRegistry.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarPluginMethodMaster;
import com.serviceplus.InstanceConfigRegistry.Entity.Master;
import com.serviceplus.InstanceConfigRegistry.Repository.AadhaarPluginMethodMasterRepository;
import com.serviceplus.InstanceConfigRegistry.Repository.MasterRepository;
import com.serviceplus.InstanceConfigRegistry.Service.RedisService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.InstanceConfigRegistry.Entity.Configuration;
import com.serviceplus.InstanceConfigRegistry.Repository.ConfigurationRepository;
import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.DEFAULT_TENANT_ID;

@Component
public class DefaultConfigLoader {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private RedisService redis;

    @Autowired
    private AadhaarPluginMethodMasterRepository aadhaarPluginMethodMasterRepository;
    
    private static final Logger registryLogger = LogManager.getLogger("registryLogger");

    @EventListener(ApplicationReadyEvent.class)
    public void loadDefaultConfigIfNotPresent() throws IOException {
        Optional<Configuration> existingConfigOpt = configurationRepository.findById(DEFAULT_TENANT_ID);
        List<Master> masterList = masterRepository.findAll();

        Resource configResource = resourceLoader.getResource("classpath:defaultconfig.json");
        try (InputStream is = configResource.getInputStream()) {
            Configuration config = objectMapper.readValue(is, Configuration.class);
            config.setTenantId(DEFAULT_TENANT_ID);
            Date now = new Date();
            config.setCreatedAt(now);
            config.setUpdatedAt(now);

            if (existingConfigOpt.isPresent() && Objects.equals(config.getData(), existingConfigOpt.get().getData())) {
                registryLogger.info("Default config already present in DB, skipping update.");

            } else {
                configurationRepository.save(config);
                redis.deleteKeysWithPrefix(DEFAULT_TENANT_ID);
                registryLogger.info("Default config loaded and saved in DB.");

            }
        }

        Resource masterResource = resourceLoader.getResource("classpath:master.json");
        try (InputStream is = masterResource.getInputStream()) {
            Master masterFromFile = objectMapper.readValue(is, Master.class);
            masterFromFile.setId(1);

            if (!masterList.isEmpty()) {
                Master masterFromDb = masterList.getFirst();

                if (!Objects.equals(masterFromFile.getData(), masterFromDb.getData())) {
                    masterFromFile.setId(masterFromDb.getId());
                    masterRepository.save(masterFromFile);
                    registryLogger.info("Master config updated in DB from JSON file.");
                } else {
                    registryLogger.info("Master config in DB is up to date, skipping update.");
                }
            } else {
                masterRepository.save(masterFromFile);
                registryLogger.info("Master config loaded and saved in DB.");
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    private void loadAadhaarPluginMethods() throws IOException {
        List<AadhaarPluginMethodMaster> pluginMethods = aadhaarPluginMethodMasterRepository.findAll();

        Resource pluginResource = resourceLoader.getResource("classpath:aadhaar_plugin_method_master.json");

        try (InputStream is = pluginResource.getInputStream()) {

            AadhaarPluginMethodMaster[] pluginMethodsFromFile = objectMapper.readValue(is, AadhaarPluginMethodMaster[].class);

            if (pluginMethodsFromFile == null || pluginMethodsFromFile.length == 0) {
                registryLogger.info("No Aadhaar plugin methods found in JSON file.");
                return;
            }

            for (AadhaarPluginMethodMaster pluginFromFile : pluginMethodsFromFile) {

                Optional<AadhaarPluginMethodMaster> pluginFromDb =
                        pluginMethods.stream()
                                .filter(plugin -> Objects.equals(
                                        plugin.getCode(),
                                        pluginFromFile.getCode()))
                                .findFirst();

                if (pluginFromDb.isPresent()) {

                    AadhaarPluginMethodMaster existing = pluginFromDb.get();

                    if (!Objects.equals(pluginFromFile.getName(), existing.getName())
                            || !Objects.equals(pluginFromFile.getDescription(), existing.getDescription())
                            || !Objects.equals(pluginFromFile.getBeanName(), existing.getBeanName())
                            || !Objects.equals(pluginFromFile.getMethodName(), existing.getMethodName())
                            || !Objects.equals(pluginFromFile.getActive(), existing.getActive())) {

                        pluginFromFile.setId(existing.getId());
                        pluginFromFile.setUpDate(new Date());

                        aadhaarPluginMethodMasterRepository.save(pluginFromFile);

                        registryLogger.info("Aadhaar plugin method updated. code={}", pluginFromFile.getCode());

                    } else {
                        registryLogger.info("Aadhaar plugin method already up to date. code={}", pluginFromFile.getCode());
                    }

                } else {

                    pluginFromFile.setCrDate(new Date());
                    pluginFromFile.setUpDate(new Date());

                    aadhaarPluginMethodMasterRepository.save(pluginFromFile);

                    registryLogger.info("Aadhaar plugin method loaded. code={}", pluginFromFile.getCode());
                }
            }
        }
    }

}
