package com.serviceplus.InstanceConfigRegistry.Repository;

import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarConfiguration;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarConfigurationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AadhaarConfigurationRepository extends MongoRepository<AadhaarConfiguration, String>, CustomRepository<AadhaarConfiguration> {

    Optional<AadhaarConfiguration> findByAuaNameAndConfigurationName(String auaName, String configurationName);

    List<AadhaarConfiguration> findByAuaName(String auaName);

    List<AadhaarConfiguration> findByStatus(AadhaarConfigurationStatus status);
}
