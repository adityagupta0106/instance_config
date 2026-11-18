package com.serviceplus.InstanceConfigRegistry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarMappingType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AadhaarNodeConfigurationResponse {

    private String nodeCode;

    private String displayName;

    private String dataType;

    private String xpath;

    private boolean enableLogs;

    private boolean maskValue;

    private AadhaarMappingType mappingType;

    private AadhaarNodeValueConfigurationResponse valueConfiguration;


    public AadhaarNodeConfigurationResponse() {
    }


    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(
            String nodeCode
    ) {
        this.nodeCode = nodeCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(
            String displayName
    ) {
        this.displayName = displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(
            String dataType
    ) {
        this.dataType = dataType;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(
            String xpath
    ) {
        this.xpath = xpath;
    }

    public boolean isEnableLogs() {
        return enableLogs;
    }

    public void setEnableLogs(
            boolean enableLogs
    ) {
        this.enableLogs = enableLogs;
    }

    public boolean isMaskValue() {
        return maskValue;
    }

    public void setMaskValue(
            boolean maskValue
    ) {
        this.maskValue = maskValue;
    }

    public AadhaarMappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(
            AadhaarMappingType mappingType
    ) {
        this.mappingType = mappingType;
    }

    public AadhaarNodeValueConfigurationResponse getValueConfiguration() {
        return valueConfiguration;
    }

    public void setValueConfiguration(
            AadhaarNodeValueConfigurationResponse valueConfiguration
    ) {
        this.valueConfiguration = valueConfiguration;
    }
}