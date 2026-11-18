package com.serviceplus.InstanceConfigRegistry.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.InstanceConfigRegistry.Entity.Configuration;

@Repository
public interface ConfigurationRepository extends MongoRepository<Configuration, String>, CustomRepository<Configuration> {

    Optional<Configuration> findByTenantId(String tenantId);
}
