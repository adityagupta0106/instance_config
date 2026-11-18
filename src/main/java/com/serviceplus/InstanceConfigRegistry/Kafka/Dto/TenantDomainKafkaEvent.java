package com.serviceplus.InstanceConfigRegistry.Kafka.Dto;

import java.util.List;

public class TenantDomainKafkaEvent {

    private String tenantId;

    private String prodUrl;

    private String preprodUrl;

    private String action;

    private Boolean active;

    private String contentSecurityPolicy;

    private Boolean ipWhitelistRequired;

    private List<String> ipWhitelist;

    private List<String> ipBlacklist;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public void setProdUrl(String prodUrl) {
        this.prodUrl = prodUrl;
    }

    public String getPreprodUrl() {
        return preprodUrl;
    }

    public void setPreprodUrl(String preprodUrl) {
        this.preprodUrl = preprodUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getContentSecurityPolicy() {
        return contentSecurityPolicy;
    }

    public void setContentSecurityPolicy(String contentSecurityPolicy) {
        this.contentSecurityPolicy = contentSecurityPolicy;
    }

    public Boolean getIpWhitelistRequired() {
        return ipWhitelistRequired;
    }

    public void setIpWhitelistRequired(Boolean ipWhitelistRequired) {
        this.ipWhitelistRequired = ipWhitelistRequired;
    }

    public List<String> getIpWhitelist() {
        return ipWhitelist;
    }

    public void setIpWhitelist(List<String> ipWhitelist) {
        this.ipWhitelist = ipWhitelist;
    }

    public List<String> getIpBlacklist() {
        return ipBlacklist;
    }

    public void setIpBlacklist(List<String> ipBlacklist) {
        this.ipBlacklist = ipBlacklist;
    }
}
