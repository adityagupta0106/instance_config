package com.serviceplus.InstanceConfigRegistry.dto;


import com.serviceplus.InstanceConfigRegistry.enums.AadhaarApiType;

public class AadhaarApiConfigurationRequest {

    private AadhaarApiType apiType;

    private String displayName;

    private String endpoint;

    private String xmlNamespace;

    private AadhaarPayloadConfigurationRequest requestPayload;

    private AadhaarPayloadConfigurationRequest responsePayload;


    public AadhaarApiConfigurationRequest() {
    }


    public AadhaarApiType getApiType() {
        return apiType;
    }

    public void setApiType(AadhaarApiType apiType) {
        this.apiType = apiType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getXmlNamespace() {
        return xmlNamespace;
    }

    public void setXmlNamespace(String xmlNamespace) {
        this.xmlNamespace = xmlNamespace;
    }

    public AadhaarPayloadConfigurationRequest getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(
            AadhaarPayloadConfigurationRequest requestPayload
    ) {
        this.requestPayload = requestPayload;
    }

    public AadhaarPayloadConfigurationRequest getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(
            AadhaarPayloadConfigurationRequest responsePayload
    ) {
        this.responsePayload = responsePayload;
    }
}
