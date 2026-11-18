package com.serviceplus.InstanceConfigRegistry.SSO.JanParichay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JanParichayTenantConfiguration {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        @JsonProperty("JANPARICHAY")
        private JanParichayConfig janParichay;

        public JanParichayConfig getJanParichay() {
            return janParichay;
        }

        public void setJanParichay(JanParichayConfig janParichay) {
            this.janParichay = janParichay;
        }
    }

    public static class JanParichayConfig {

        @JsonProperty("client_serviceId")
        private String serviceId;

        @JsonProperty("client_rest_auth_id")
        private String restAuthId;

        @JsonProperty("client_id")
        private String clientId;

        @JsonProperty("client_secret")
        private String clientSecret;

        @JsonProperty("client_auth_key")
        private String authKey;

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getRestAuthId() {
            return restAuthId;
        }

        public void setRestAuthId(String restAuthId) {
            this.restAuthId = restAuthId;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getAuthKey() {
            return authKey;
        }

        public void setAuthKey(String authKey) {
            this.authKey = authKey;
        }
    }
}
