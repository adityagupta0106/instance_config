package com.serviceplus.InstanceConfigRegistry.Kafka.Producers;

import com.serviceplus.InstanceConfigRegistry.Kafka.Dto.TenantDomainKafkaEvent;
import com.serviceplus.InstanceConfigRegistry.Kafka.Service.KafkaOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TenantDomainKafkaProducer {

    @Value("${kafka.topic.new-tenant-onboard}")
    private String KAFKA_TOPIC_NEW_TENANT_ONBOARD;

    private final KafkaOutboxService kafkaOutboxService;

    @Autowired
    public TenantDomainKafkaProducer(KafkaOutboxService kafkaOutboxService) {
        this.kafkaOutboxService = kafkaOutboxService;
    }

    public void publishTenantDomainEvent(
            String tenantId,
            String prodUrl,
            String preprodUrl,
            String action,
            Boolean active,
            String contentSecurityPolicy,
            Boolean ipWhitelistRequired,
            List<String> ipWhitelist,
            List<String> ipBlacklist) {

        TenantDomainKafkaEvent event = new TenantDomainKafkaEvent();

        event.setTenantId(tenantId);
        event.setProdUrl(prodUrl);
        event.setPreprodUrl(preprodUrl);
        event.setAction(action);

        event.setActive(active);
        event.setContentSecurityPolicy(contentSecurityPolicy);
        event.setIpWhitelistRequired(ipWhitelistRequired);
        event.setIpWhitelist(ipWhitelist != null ? ipWhitelist : Collections.emptyList());
        event.setIpBlacklist(ipBlacklist != null ? ipBlacklist : Collections.emptyList());

        kafkaOutboxService.saveEvent(KAFKA_TOPIC_NEW_TENANT_ONBOARD, event);
    }
}
