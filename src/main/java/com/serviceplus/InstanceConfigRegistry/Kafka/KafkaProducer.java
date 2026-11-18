package com.serviceplus.InstanceConfigRegistry.Kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.entityToString;

@Service
public class KafkaProducer {

    private static final Logger KafkaLogger = LogManager.getLogger("KafkaLogger");

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String key, String payload) {

        try {

            KafkaLogger.info("Sending kafka message to topic {} with key {}", topic, key);

            kafkaTemplate.send(topic, key, payload).whenComplete((result, ex) -> {

                if (ex != null) {
                    KafkaLogger.error("Kafka publish failed for topic {} error {}", topic, ex.getMessage(), ex);
                } else {
                    KafkaLogger.info("Kafka publish success for topic {} offset {}", topic, result.getRecordMetadata().offset());
                }
            });

        } catch (Exception e) {
            KafkaLogger.error("Unable to publish kafka message for topic {} error {}", topic, e.getMessage(), e);
        }
    }
}
