package com.serviceplus.InstanceConfigRegistry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AadhaarNodeValueConfigurationResponse {

    private String value;

    private String systemVariable;

    private String pluginMethodCode;

    private boolean visibleToServiceDefiner;


    public AadhaarNodeValueConfigurationResponse() {
    }


    public String getValue() {
        return value;
    }

    public void setValue(
            String value
    ) {
        this.value = value;
    }

    public String getSystemVariable() {
        return systemVariable;
    }

    public void setSystemVariable(
            String systemVariable
    ) {
        this.systemVariable = systemVariable;
    }

    public boolean isVisibleToServiceDefiner() {
        return visibleToServiceDefiner;
    }

    public void setVisibleToServiceDefiner(
            boolean visibleToServiceDefiner
    ) {
        this.visibleToServiceDefiner = visibleToServiceDefiner;
    }

    public String getPluginMethodCode() {
        return pluginMethodCode;
    }

    public void setPluginMethodCode(String pluginMethodCode) {
        this.pluginMethodCode = pluginMethodCode;
    }
}