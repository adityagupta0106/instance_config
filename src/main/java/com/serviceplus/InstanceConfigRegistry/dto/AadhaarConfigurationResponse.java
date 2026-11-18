package com.serviceplus.InstanceConfigRegistry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarAuthenticationType;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarConfigurationMode;
import com.serviceplus.InstanceConfigRegistry.enums.AadhaarConfigurationStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AadhaarConfigurationResponse {

    private String id;

    private String auaName;

    private String configurationName;

    private AadhaarAuthenticationType authenticationType;

    private List<AadhaarConfigurationMode> enabledModes = new ArrayList<>();

    private TransactionIdConfigurationResponse transactionIdConfiguration;

    private List<AadhaarApiConfigurationResponse> apiConfigurations = new ArrayList<>();

    private AadhaarConfigurationStatus status;

    private Integer version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;


    public AadhaarConfigurationResponse() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public TransactionIdConfigurationResponse getTransactionIdConfiguration() {
        return transactionIdConfiguration;
    }

    public void setTransactionIdConfiguration(TransactionIdConfigurationResponse transactionIdConfiguration) {
        this.transactionIdConfiguration = transactionIdConfiguration;
    }

    public List<AadhaarApiConfigurationResponse> getApiConfigurations() {
        return apiConfigurations;
    }

    public void setApiConfigurations(List<AadhaarApiConfigurationResponse> apiConfigurations) {
        this.apiConfigurations = apiConfigurations;
    }

    public AadhaarConfigurationStatus getStatus() {
        return status;
    }

    public void setStatus(AadhaarConfigurationStatus status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}