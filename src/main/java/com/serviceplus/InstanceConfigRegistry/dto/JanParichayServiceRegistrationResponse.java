package com.serviceplus.InstanceConfigRegistry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class JanParichayServiceRegistrationResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        @SerializedName("AESIV")
        private String aesIV;

        @SerializedName("AuthKey")
        private String authKey;

        @SerializedName("RestAuthId")
        private String restAuthId;

        @SerializedName("ClientSecret")
        private String clientSecret;

        @SerializedName("ClientId")
        private String clientId;

        @SerializedName("ServiceType")
        private String serviceType;

        public String getAesIV() {
            return aesIV;
        }

        public void setAesIV(String aesIV) {
            this.aesIV = aesIV;
        }

        public String getAuthKey() {
            return authKey;
        }

        public void setAuthKey(String authKey) {
            this.authKey = authKey;
        }

        public String getRestAuthId() {
            return restAuthId;
        }

        public void setRestAuthId(String restAuthId) {
            this.restAuthId = restAuthId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }
    }
}
