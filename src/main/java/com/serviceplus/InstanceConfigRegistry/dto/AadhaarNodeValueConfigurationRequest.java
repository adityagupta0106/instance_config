package com.serviceplus.InstanceConfigRegistry.dto;


public class AadhaarNodeValueConfigurationRequest {

    private String value;

    private String systemVariable;

    private String pluginMethodCode;


    public AadhaarNodeValueConfigurationRequest() {
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSystemVariable() {
        return systemVariable;
    }

    public void setSystemVariable(String systemVariable) {
        this.systemVariable = systemVariable;
    }

    public String getPluginMethodCode() {
        return pluginMethodCode;
    }

    public void setPluginMethodCode(String pluginMethodCode) {
        this.pluginMethodCode = pluginMethodCode;
    }
}
