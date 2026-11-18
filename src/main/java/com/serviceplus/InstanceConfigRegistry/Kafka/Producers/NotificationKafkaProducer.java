package com.serviceplus.InstanceConfigRegistry.Kafka.Producers;

import com.serviceplus.InstanceConfigRegistry.Config.NotificationProperties;
import com.serviceplus.InstanceConfigRegistry.Kafka.Service.KafkaOutboxService;
import com.serviceplus.InstanceConfigRegistry.dto.NotificationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:alerts.properties")
public class NotificationKafkaProducer {

    private static final Logger logger =  LoggerFactory.getLogger("tenantConfiguration");

    private final KafkaOutboxService kafkaOutboxService;

    private final NotificationProperties notificationProperties;

    @Value("${notification.kafka.topic}")
    private String notificationTopic;

    @Value("${tenant.admin.email.subject}")
    private String tenantAdminEmailSubject;

    @Value("${tenant.admin.email.body}")
    private String tenantAdminEmailBody;

    @Value("${tenant.admin.password.sms}")
    private String tenantAdminPasswordSms;

    public NotificationKafkaProducer(KafkaOutboxService kafkaOutboxService, NotificationProperties notificationProperties) {
        this.kafkaOutboxService = kafkaOutboxService;
        this.notificationProperties = notificationProperties;
    }

    public void sendTenantAdminUserId(String tenantId, String email, String username, String correlationId) {

        NotificationRequestDTO request = new NotificationRequestDTO();

        request.setTenantId(tenantId);
        request.setChannel("EMAIL");
        request.setProvider("SMTP");
        request.setAdapterCode("EMAIL");
        request.setCorrelationId(correlationId);

        Map<String, Object> recipient = new HashMap<>();
        recipient.put("to", List.of(email));

        request.setRecipient(recipient);

        Map<String, Object> payload = new HashMap<>();

        payload.put("subject", tenantAdminEmailSubject);

        payload.put("body", tenantAdminEmailBody.formatted(username));

        request.setPayload(payload);

        request.setConfiguration(getEmailConfiguration());

        kafkaOutboxService.saveEvent(notificationTopic, request);

        logger.info("Tenant Admin User ID notification queued. tenantId={}, email={}", tenantId, email);
    }


    public void sendTenantAdminPassword(String tenantId, String mobile, String password, String correlationId) {

        NotificationRequestDTO request = new NotificationRequestDTO();

        request.setTenantId(tenantId);
        request.setChannel("SMS");
        request.setProvider("NIC");
        request.setAdapterCode("SMS");
        request.setCorrelationId(correlationId);

        Map<String, Object> recipient = new HashMap<>();
        recipient.put("mobile", mobile);

        request.setRecipient(recipient);

        Map<String, Object> payload = new HashMap<>();

        String message = tenantAdminPasswordSms.formatted(password);

        payload.put("message", message);

        request.setPayload(payload);

        request.setConfiguration(getSmsConfiguration());

        kafkaOutboxService.saveEvent(notificationTopic, request);

        logger.info("Tenant Admin password notification queued. tenantId={}, mobile={}", tenantId, mobile);
    }

    private Map<String, Object> getEmailConfiguration() {

        NotificationProperties.Email email = notificationProperties.getEmail();

        Map<String, Object> configuration = new HashMap<>();

        configuration.put("smtp_host", email.getSmtpHost());
        configuration.put("smtp_port", email.getSmtpPort());
        configuration.put("smtp_auth_user", email.getSmtpAuthUser());
        configuration.put("smtp_auth_password", email.getSmtpAuthPassword());
        configuration.put("smtp_ssl_protocols", email.getSmtpSslProtocols());
        configuration.put("smtp_mail_from", email.getSmtpMailFrom());

        return configuration;
    }

    private Map<String, Object> getSmsConfiguration() {

        NotificationProperties.Sms sms = notificationProperties.getSms();

        Map<String, Object> configuration = new HashMap<>();

        configuration.put("url", sms.getUrl());
        configuration.put("username", sms.getUsername());
        configuration.put("password", sms.getPassword());
        configuration.put("signatureId", sms.getSignatureId());
        configuration.put("dltEntityId", sms.getDltEntityId());
        configuration.put("dltTemplateId", sms.getDltTemplateId());

        return configuration;
    }
}
