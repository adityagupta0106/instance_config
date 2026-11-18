package com.serviceplus.InstanceConfigRegistry.Entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserTenantMapping implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	private Long userId;

	private String tenantId;

	@Id
	private String tenant;

    private String tenantName;

    @Transient
    private String preprodUrl;

    @Transient
    private String prodUrl;

    @Transient
    private String spocName;

    @Transient
    private String spocEmail;

    @Transient
    private String spocMobile;

    @Transient
    private String tenantUserId;


    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getSpocName() {
        return spocName;
    }

    public void setSpocName(String spocName) {
        this.spocName = spocName;
    }

    public String getPreprodUrl() {
        return preprodUrl;
    }

    public void setPreprodUrl(String preprodUrl) {
        this.preprodUrl = preprodUrl;
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public void setProdUrl(String prodUrl) {
        this.prodUrl = prodUrl;
    }

    public String getSpocEmail() {
        return spocEmail;
    }

    public void setSpocEmail(String spocEmail) {
        this.spocEmail = spocEmail;
    }

    public String getSpocMobile() {
        return spocMobile;
    }

    public void setSpocMobile(String spocMobile) {
        this.spocMobile = spocMobile;
    }

    public String getTenantUserId() {
        return tenantUserId;
    }

    public void setTenantUserId(String tenantUserId) {
        this.tenantUserId = tenantUserId;
    }
}
