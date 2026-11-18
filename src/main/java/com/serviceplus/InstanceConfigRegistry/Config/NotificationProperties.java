package com.serviceplus.InstanceConfigRegistry.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private Email email = new Email();

    private Sms sms = new Sms();

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }


    public static class Email {

        private String provider;

        private String smtpHost;

        private String smtpPort;

        private String smtpAuthUser;

        private String smtpAuthPassword;

        private String smtpSslProtocols;

        private String smtpMailFrom;


        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getSmtpHost() {
            return smtpHost;
        }

        public void setSmtpHost(String smtpHost) {
            this.smtpHost = smtpHost;
        }

        public String getSmtpPort() {
            return smtpPort;
        }

        public void setSmtpPort(String smtpPort) {
            this.smtpPort = smtpPort;
        }

        public String getSmtpAuthUser() {
            return smtpAuthUser;
        }

        public void setSmtpAuthUser(String smtpAuthUser) {
            this.smtpAuthUser = smtpAuthUser;
        }

        public String getSmtpAuthPassword() {
            return smtpAuthPassword;
        }

        public void setSmtpAuthPassword(String smtpAuthPassword) {
            this.smtpAuthPassword = smtpAuthPassword;
        }

        public String getSmtpSslProtocols() {
            return smtpSslProtocols;
        }

        public void setSmtpSslProtocols(String smtpSslProtocols) {
            this.smtpSslProtocols = smtpSslProtocols;
        }

        public String getSmtpMailFrom() {
            return smtpMailFrom;
        }

        public void setSmtpMailFrom(String smtpMailFrom) {
            this.smtpMailFrom = smtpMailFrom;
        }
    }


    public static class Sms {

        private String provider;

        private String url;

        private String username;

        private String password;

        private String signatureId;

        private String dltEntityId;

        private String dltTemplateId;


        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSignatureId() {
            return signatureId;
        }

        public void setSignatureId(String signatureId) {
            this.signatureId = signatureId;
        }

        public String getDltEntityId() {
            return dltEntityId;
        }

        public void setDltEntityId(String dltEntityId) {
            this.dltEntityId = dltEntityId;
        }

        public String getDltTemplateId() {
            return dltTemplateId;
        }

        public void setDltTemplateId(String dltTemplateId) {
            this.dltTemplateId = dltTemplateId;
        }
    }
}