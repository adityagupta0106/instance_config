package com.serviceplus.InstanceConfigRegistry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceRegistrationRequest {

    @SerializedName("ServiceId")
    private String serviceId;

    @SerializedName("Description")
    private String description;

    @SerializedName("DisplayName")
    private String displayName;

    @SerializedName("EnforceMultiFactor")
    private Integer enforceMultiFactor;

    @SerializedName("ForceMultiFactor")
    private Integer forceMultiFactor;

    @SerializedName("HomeUrl")
    private String homeUrl;

    @SerializedName("LoginType")
    private Integer loginType;

    @SerializedName("LoginUrl")
    private String loginUrl;

    @SerializedName("LogoutUrl")
    private String logoutUrl;

    @SerializedName("ServiceCategory")
    private String serviceCategory;

    @SerializedName("ServiceOwnerName")
    private String serviceOwnerName;

    @SerializedName("ServiceOwnerEmailId")
    private String serviceOwnerEmailId;

    @SerializedName("ServiceOwnerMobileNo")
    private String serviceOwnerMobileNo;

    @SerializedName("ServiceOwnerUId")
    private String serviceOwnerUId;

    @SerializedName("ServiceState")
    private String serviceState;

    @SerializedName("Environment")
    private String environment;

    @SerializedName("SubService")
    private String subService;

    @SerializedName("IsSDL")
    private String isSDL;

    @SerializedName("ACRValues")
    private String acrValues;

    @SerializedName("ConsentValues")
    private String consentValues;

    @SerializedName("EFileNo")
    private String eFileNo;

    @SerializedName("SSOType")
    private String ssoType;

    @SerializedName("AlternateRedirectURIs")
    private List<String> alternateRedirectURIs;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getEnforceMultiFactor() {
        return enforceMultiFactor;
    }

    public void setEnforceMultiFactor(Integer enforceMultiFactor) {
        this.enforceMultiFactor = enforceMultiFactor;
    }

    public Integer getForceMultiFactor() {
        return forceMultiFactor;
    }

    public void setForceMultiFactor(Integer forceMultiFactor) {
        this.forceMultiFactor = forceMultiFactor;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getServiceOwnerName() {
        return serviceOwnerName;
    }

    public void setServiceOwnerName(String serviceOwnerName) {
        this.serviceOwnerName = serviceOwnerName;
    }

    public String getServiceOwnerEmailId() {
        return serviceOwnerEmailId;
    }

    public void setServiceOwnerEmailId(String serviceOwnerEmailId) {
        this.serviceOwnerEmailId = serviceOwnerEmailId;
    }

    public String getServiceOwnerMobileNo() {
        return serviceOwnerMobileNo;
    }

    public void setServiceOwnerMobileNo(String serviceOwnerMobileNo) {
        this.serviceOwnerMobileNo = serviceOwnerMobileNo;
    }

    public String getServiceOwnerUId() {
        return serviceOwnerUId;
    }

    public void setServiceOwnerUId(String serviceOwnerUId) {
        this.serviceOwnerUId = serviceOwnerUId;
    }

    public String getServiceState() {
        return serviceState;
    }

    public void setServiceState(String serviceState) {
        this.serviceState = serviceState;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getSubService() {
        return subService;
    }

    public void setSubService(String subService) {
        this.subService = subService;
    }

    public String getIsSDL() {
        return isSDL;
    }

    public void setIsSDL(String isSDL) {
        this.isSDL = isSDL;
    }

    public String getAcrValues() {
        return acrValues;
    }

    public void setAcrValues(String acrValues) {
        this.acrValues = acrValues;
    }

    public String getConsentValues() {
        return consentValues;
    }

    public void setConsentValues(String consentValues) {
        this.consentValues = consentValues;
    }

    public String getEFileNo() {
        return eFileNo;
    }

    public void setEFileNo(String eFileNo) {
        this.eFileNo = eFileNo;
    }

    public String getSsoType() {
        return ssoType;
    }

    public void setSsoType(String ssoType) {
        this.ssoType = ssoType;
    }

    public String geteFileNo() {
        return eFileNo;
    }

    public void seteFileNo(String eFileNo) {
        this.eFileNo = eFileNo;
    }

    public List<String> getAlternateRedirectURIs() {
        return alternateRedirectURIs;
    }

    public void setAlternateRedirectURIs(List<String> alternateRedirectURIs) {
        this.alternateRedirectURIs = alternateRedirectURIs;
    }
}