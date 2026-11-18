package com.serviceplus.InstanceConfigRegistry.dto;

import com.serviceplus.InstanceConfigRegistry.Entity.TransactionIdConfiguration;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarAuthenticationType;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarConfigurationMode;

import java.util.ArrayList;
import java.util.List;

public class AadhaarConfigurationRequest {

    private String id;

    private String auaName;

    private String configurationName;

    private AadhaarAuthenticationType authenticationType;

    private List<AadhaarConfigurationMode> enabledModes = new ArrayList<>();

    private TransactionIdConfiguration transactionIdConfiguration;

    private List<AadhaarApiConfigurationRequest> apiConfigurations = new ArrayList<>();


    public AadhaarConfigurationRequest() {
    }


    public String getAuaName() {
        return auaName;
    }

    public void setAuaName(String auaName) {
        this.auaName = auaName;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public AadhaarAuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AadhaarAuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public List<AadhaarConfigurationMode> getEnabledModes() {
        return enabledModes;
    }

    public void setEnabledModes(List<AadhaarConfigurationMode> enabledModes) {
        this.enabledModes = enabledModes;
    }

    public TransactionIdConfiguration getTransactionIdConfiguration() {
        return transactionIdConfiguration;
    }

    public void setTransactionIdConfiguration(TransactionIdConfiguration transactionIdConfiguration) {
        this.transactionIdConfiguration = transactionIdConfiguration;
    }

    public List<AadhaarApiConfigurationRequest> getApiConfigurations() {
        return apiConfigurations;
    }

    public void setApiConfigurations(List<AadhaarApiConfigurationRequest> apiConfigurations) {
        this.apiConfigurations = apiConfigurations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
