package com.serviceplus.InstanceConfigRegistry.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.InstanceConfigRegistry.Entity.UserTenantMapping;

@Repository
public interface UserTenantMappingRepository  extends MongoRepository<UserTenantMapping, String> ,CustomRepository<UserTenantMapping> {

	List<UserTenantMapping> findByUserId(Long userID);
	List<UserTenantMapping> findAll();
	
}
