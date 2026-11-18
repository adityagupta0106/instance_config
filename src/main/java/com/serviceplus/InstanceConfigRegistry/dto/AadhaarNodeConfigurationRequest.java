package com.serviceplus.InstanceConfigRegistry.dto;


import com.serviceplus.InstanceConfigRegistry.enums.AadhaarMappingType;

public class AadhaarNodeConfigurationRequest {

    private String nodeCode;

    private String displayName;

    private String dataType;

    private String xpath;

    private boolean enableLogs;

    private boolean maskValue;

    private AadhaarMappingType mappingType;

    private AadhaarNodeValueConfigurationRequest valueConfiguration;


    public AadhaarNodeConfigurationRequest() {
    }


    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public boolean isEnableLogs() {
        return enableLogs;
    }

    public void setEnableLogs(boolean enableLogs) {
        this.enableLogs = enableLogs;
    }

    public boolean isMaskValue() {
        return maskValue;
    }

    public void setMaskValue(boolean maskValue) {
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

    public AadhaarNodeValueConfigurationRequest getValueConfiguration() {
        return valueConfiguration;
    }

    public void setValueConfiguration(
            AadhaarNodeValueConfigurationRequest valueConfiguration
    ) {
        this.valueConfiguration = valueConfiguration;
    }
}
