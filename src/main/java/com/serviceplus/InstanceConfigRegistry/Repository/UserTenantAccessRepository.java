package com.serviceplus.InstanceConfigRegistry.Repository;

import com.serviceplus.InstanceConfigRegistry.Entity.UserTenantAccess;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTenantAccessRepository extends MongoRepository<UserTenantAccess, String> {

    List<UserTenantAccess> findByLoginIdAndActiveTrue(String loginId);

    Optional<UserTenantAccess> findByTenantIdAndLoginIdAndActiveTrue(String tenantId, String loginId);

    Optional<UserTenantAccess> findByTenantId(String tenantId);
}
