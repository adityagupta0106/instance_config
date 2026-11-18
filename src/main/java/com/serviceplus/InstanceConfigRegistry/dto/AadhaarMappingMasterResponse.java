package com.serviceplus.InstanceConfigRegistry.dto;

import java.io.Serializable;
import java.util.List;

public class AadhaarMappingMasterResponse implements Serializable {

    private List<MasterOptionResponse> pluginMethods;
    private List<MasterOptionResponse> systemGeneratedFields;
    private List<MasterOptionResponse> configurationModes;
    private List<MasterOptionResponse> mappingTypes;


    public AadhaarMappingMasterResponse() {
    }

    public List<MasterOptionResponse> getPluginMethods() {
        return pluginMethods;
    }

    public void setPluginMethods(List<MasterOptionResponse> pluginMethods) {
        this.pluginMethods = pluginMethods;
    }

    public List<MasterOptionResponse> getSystemGeneratedFields() {
        return systemGeneratedFields;
    }

    public void setSystemGeneratedFields(List<MasterOptionResponse> systemGeneratedFields) {
        this.systemGeneratedFields = systemGeneratedFields;
    }

    public List<MasterOptionResponse> getConfigurationModes() {
        return configurationModes;
    }

    public void setConfigurationModes(List<MasterOptionResponse> configurationModes) {
        this.configurationModes = configurationModes;
    }

    public List<MasterOptionResponse> getMappingTypes() {
        return mappingTypes;
    }

    public void setMappingTypes(List<MasterOptionResponse> mappingTypes) {
        this.mappingTypes = mappingTypes;
    }
}