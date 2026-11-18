package com.serviceplus.InstanceConfigRegistry.dto;

import com.serviceplus.InstanceConfigRegistry.enums.AadhaarConfigurationMode;

import java.time.LocalDateTime;
import java.util.List;

public class AUAListing {

    private String auaName;
    private String id;
    private List<AadhaarConfigurationMode> enabledModes;
    private LocalDateTime lastUpdated;

    public String getAuaName() {
        return auaName;
    }

    public void setAuaName(String auaName) {
        this.auaName = auaName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AadhaarConfigurationMode> getEnabledModes() {
        return enabledModes;
    }

    public void setEnabledModes(List<AadhaarConfigurationMode> enabledModes) {
        this.enabledModes = enabledModes;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
