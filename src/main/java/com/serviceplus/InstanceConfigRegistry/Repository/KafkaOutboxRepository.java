package com.serviceplus.InstanceConfigRegistry.Repository;

import java.util.List;

import com.serviceplus.InstanceConfigRegistry.Entity.KafkaOutboxEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaOutboxRepository extends MongoRepository<KafkaOutboxEvent, String> {
    List<KafkaOutboxEvent> findTop100ByStatusInOrderByCreatedAtAsc(List<String> status);
}
