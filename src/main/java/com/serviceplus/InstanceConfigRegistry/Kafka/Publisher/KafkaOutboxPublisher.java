package com.serviceplus.InstanceConfigRegistry.Kafka.Publisher;

import com.serviceplus.InstanceConfigRegistry.Entity.KafkaOutboxEvent;
import com.serviceplus.InstanceConfigRegistry.Kafka.KafkaProducer;
import com.serviceplus.InstanceConfigRegistry.Repository.KafkaOutboxRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.*;

@Service
public class KafkaOutboxPublisher {

    private static final Logger logger = LogManager.getLogger(KafkaOutboxPublisher.class);

    private final KafkaOutboxRepository repository;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public KafkaOutboxPublisher(KafkaOutboxRepository repository, KafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<KafkaOutboxEvent> events = repository.findTop100ByStatusInOrderByCreatedAtAsc(List.of(STATUS_NEW,STATUS_FAILED));
        if (events.isEmpty()) {
            return;
        }

        logger.info("Outbox events found={}", events.size());

        for (KafkaOutboxEvent event : events) {

            try {

                event.setStatus(STATUS_PROCESSING);
                repository.save(event);
                kafkaProducer.sendMessage(event.getTopic(), event.getEventKey(), event.getPayload());
                event.setStatus(STATUS_COMPLETED);
                event.setProcessedAt(Instant.now());

                repository.save(event);

                logger.info("Outbox event published id={}", event.getId());

            } catch (Exception ex) {

                logger.error("Failed to publish outbox event id={}", event.getId(), ex);

                event.setStatus(STATUS_FAILED);
                event.setRetryCount(event.getRetryCount() + 1);
                event.setProcessedAt(Instant.now());

                repository.save(event);
            }
        }
    }
}
