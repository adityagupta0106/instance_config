package com.serviceplus.InstanceConfigRegistry.dto;

public class TenantResponse {

    private String tenantId;

    private String tenantName;

    private String prodUrl;

    private String preprodUrl;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
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
}
