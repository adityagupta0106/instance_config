package com.serviceplus.InstanceConfigRegistry.centralrepo.dto;

import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoActionType;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoServiceStatus;

import java.io.Serializable;
import java.util.Date;

public class ReviewHistoryItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Integer serviceId;
    private String serviceName;
    private String serviceAbbreviation;
    private String sourceTenantName;
    private String sourceDepartmentName;
    private Integer versionNo;
    private Integer minorVersionNo;
    private RepoServiceStatus status;
    private RepoActionType actionType;
    private String pushedByName;
    private Date pushedDate;
    private String reviewedByName;
    private Date reviewedDate;
    private String rejectionReason;

    public ReviewHistoryItemDTO() {
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

    public String getSourceTenantName() {
        return sourceTenantName;
    }

    public void setSourceTenantName(String sourceTenantName) {
        this.sourceTenantName = sourceTenantName;
    }

    public String getSourceDepartmentName() {
        return sourceDepartmentName;
    }

    public void setSourceDepartmentName(String sourceDepartmentName) {
        this.sourceDepartmentName = sourceDepartmentName;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getMinorVersionNo() {
        return minorVersionNo;
    }

    public void setMinorVersionNo(Integer minorVersionNo) {
        this.minorVersionNo = minorVersionNo;
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

    public String getPushedByName() {
        return pushedByName;
    }

    public void setPushedByName(String pushedByName) {
        this.pushedByName = pushedByName;
    }

    public Date getPushedDate() {
        return pushedDate;
    }

    public void setPushedDate(Date pushedDate) {
        this.pushedDate = pushedDate;
    }

    public String getReviewedByName() {
        return reviewedByName;
    }

    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
    }

    public Date getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(Date reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
