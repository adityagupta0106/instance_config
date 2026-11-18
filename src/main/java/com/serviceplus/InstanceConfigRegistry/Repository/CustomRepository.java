package com.serviceplus.InstanceConfigRegistry.Repository;

import com.mongodb.client.ClientSession;

public interface CustomRepository<T> {
    void deleteByTenantId(String tenantId, ClientSession session,Class<T> clazz,String key);
}
