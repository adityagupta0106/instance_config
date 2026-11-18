package com.serviceplus.InstanceConfigRegistry.dto;

public class JanParichayServiceRegistrationRequest {

    private String serviceId;
    private String description;
    private String displayName;
    private Integer enforceMultiFactor = 0;
    private Integer forceMultiFactor = 0;

    private String homeUrl;
    private Integer loginType = 1;
    private String loginUrl;
    private String logoutUrl;

    private String serviceCategory;

    private String serviceOwnerName;
    private String serviceOwnerEmailId;
    private String serviceOwnerMobileNo;
    private String serviceOwnerUId;

    private String serviceState;
    private String environment = "STAG";
    private String subService = "1";

    private String isSDL = "0";
    private String acrValues = "000000";
    private String consentValues = "00000";
    private String eFileNo;
    private String ssoType = "Oauth";

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
}
