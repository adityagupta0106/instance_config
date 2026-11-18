package com.serviceplus.InstanceConfigRegistry.centralrepo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoActionType;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoServiceStatus;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CentralRepoListItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Integer serviceId;
    private String serviceName;
    private String serviceAbbreviation;
    private String sourceTenantId;
    private String sourceTenantName;
    private Long sourceDepartmentId;
    private String sourceDepartmentName;
    private Integer versionNo;
    private Integer minorVersionNo;
    private Long submittedApplicationsCount;
    private RepoServiceStatus status;
    private String rejectionReason;
    private String pushedByName;
    private Date pushedDate;
    private String reviewedByName;
    private Date reviewedDate;
    private RepoActionType actionType;
    private Integer slaDays;
    private Date createdAt;

    private Long entityId;
    private String entityName;
    private String departmentName;
    private String tenantName;

    public CentralRepoListItemDTO() {
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

    public String getSourceTenantId() {
        return sourceTenantId;
    }

    public void setSourceTenantId(String sourceTenantId) {
        this.sourceTenantId = sourceTenantId;
    }

    public String getSourceTenantName() {
        return sourceTenantName;
    }

    public void setSourceTenantName(String sourceTenantName) {
        this.sourceTenantName = sourceTenantName;
    }

    public Long getSourceDepartmentId() {
        return sourceDepartmentId;
    }

    public void setSourceDepartmentId(Long sourceDepartmentId) {
        this.sourceDepartmentId = sourceDepartmentId;
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

    public Long getSubmittedApplicationsCount() {
        return submittedApplicationsCount;
    }

    public void setSubmittedApplicationsCount(Long submittedApplicationsCount) {
        this.submittedApplicationsCount = submittedApplicationsCount;
    }

    public RepoServiceStatus getStatus() {
        return status;
    }

    public void setStatus(RepoServiceStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    public RepoActionType getActionType() {
        return actionType;
    }

    public void setActionType(RepoActionType actionType) {
        this.actionType = actionType;
    }

    public Integer getSlaDays() {
        return slaDays;
    }

    public void setSlaDays(Integer slaDays) {
        this.slaDays = slaDays;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
