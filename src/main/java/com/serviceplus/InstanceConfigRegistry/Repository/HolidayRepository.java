package com.serviceplus.InstanceConfigRegistry.Repository;

import com.serviceplus.InstanceConfigRegistry.Entity.HolidayDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HolidayRepository extends MongoRepository<HolidayDocument, String> {
    List<HolidayDocument> findByTenantId(String tenantId);
}
