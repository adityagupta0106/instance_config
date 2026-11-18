package com.serviceplus.InstanceConfigRegistry.dto;

import com.serviceplus.InstanceConfigRegistry.enums.AadhaarApiType;

public class AadhaarApiConfigurationResponse {

    private AadhaarApiType apiType;

    private String displayName;

    private String endpoint;

    private String xmlNamespace;

    private AadhaarPayloadConfigurationResponse requestPayload;

    private AadhaarPayloadConfigurationResponse responsePayload;


    public AadhaarApiConfigurationResponse() {
    }


    public AadhaarApiType getApiType() {
        return apiType;
    }

    public void setApiType(
            AadhaarApiType apiType
    ) {
        this.apiType = apiType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(
            String displayName
    ) {
        this.displayName = displayName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(
            String endpoint
    ) {
        this.endpoint = endpoint;
    }

    public String getXmlNamespace() {
        return xmlNamespace;
    }

    public void setXmlNamespace(
            String xmlNamespace
    ) {
        this.xmlNamespace = xmlNamespace;
    }

    public AadhaarPayloadConfigurationResponse getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(
            AadhaarPayloadConfigurationResponse requestPayload
    ) {
        this.requestPayload = requestPayload;
    }

    public AadhaarPayloadConfigurationResponse getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(
            AadhaarPayloadConfigurationResponse responsePayload
    ) {
        this.responsePayload = responsePayload;
    }
}