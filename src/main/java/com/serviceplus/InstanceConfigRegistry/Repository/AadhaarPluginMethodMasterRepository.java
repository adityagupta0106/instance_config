package com.serviceplus.InstanceConfigRegistry.Repository;

import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarPluginMethodMaster;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AadhaarPluginMethodMasterRepository extends MongoRepository<AadhaarPluginMethodMaster, String> {

    Optional<AadhaarPluginMethodMaster> findByCodeAndActive(String code, Boolean active);

    List<AadhaarPluginMethodMaster> findByActive(Boolean active);

    List<AadhaarPluginMethodMaster> findByActiveTrue();
}
