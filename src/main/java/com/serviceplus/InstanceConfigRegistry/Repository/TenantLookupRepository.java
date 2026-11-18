package com.serviceplus.InstanceConfigRegistry.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.InstanceConfigRegistry.Entity.TenantLookUp;

@Repository
public interface TenantLookupRepository  extends MongoRepository<TenantLookUp, String> ,CustomRepository<TenantLookUp>{
}
