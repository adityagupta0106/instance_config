package com.serviceplus.InstanceConfigRegistry.Kafka.Service;

import com.serviceplus.InstanceConfigRegistry.Entity.KafkaOutboxEvent;
import com.serviceplus.InstanceConfigRegistry.Repository.KafkaOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.OUTBOX_TOKEN_PREFIX;
import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.STATUS_NEW;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.*;

@Service
public class KafkaOutboxService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaOutboxService.class);

    @Autowired
    private KafkaOutboxRepository repository;

    public void saveEvent(String topic, Object payload) {

        try {

            KafkaOutboxEvent event = new KafkaOutboxEvent();

            event.setId(generateUniqueToken(OUTBOX_TOKEN_PREFIX));
            event.setTopic(topic);
            event.setEventKey(event.getId());
            event.setPayload(entityToStringDisableHtmlEscape(payload));
            event.setStatus(STATUS_NEW);
            event.setRetryCount(0);
            event.setCreatedAt(Instant.now());
            event.setProcessedAt(Instant.now());

            repository.save(event);

            logger.info("Kafka outbox event saved successfully for topic : {}", topic);

        } catch (Exception e) {
            logger.error("Unable to save kafka outbox event : {}", e.getMessage(), e);
        }
    }
}
