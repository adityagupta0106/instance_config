package com.serviceplus.InstanceConfigRegistry.centralrepo.dto;

import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoActionType;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoServiceStatus;

import java.io.Serializable;
import java.util.Date;

public class CentralRepoActionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Integer serviceId;
    private String serviceName;
    private String serviceAbbreviation;
    private RepoServiceStatus status;
    private RepoActionType actionType;
    private String sourceDepartmentName;
    private String actionByName;
    private Date actionDate;
    private String rejectionReason;
    private String message;

    public CentralRepoActionResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceAbbreviation() {
        return serviceAbbreviation;
    }

    public void setServiceAbbreviation(String serviceAbbreviation) {
        this.serviceAbbreviation = serviceAbbreviation;
    }

    public RepoServiceStatus getStatus() {
        return status;
    }

    public void setStatus(RepoServiceStatus status) {
        this.status = status;
    }

    public RepoActionType getActionType() {
        return actionType;
    }

    public void setActionType(RepoActionType actionType) {
        this.actionType = actionType;
    }

    public String getSourceDepartmentName() {
        return sourceDepartmentName;
    }

    public void setSourceDepartmentName(String sourceDepartmentName) {
        this.sourceDepartmentName = sourceDepartmentName;
    }

    public String getActionByName() {
        return actionByName;
    }

    public void setActionByName(String actionByName) {
        this.actionByName = actionByName;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
