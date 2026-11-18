package com.serviceplus.InstanceConfigRegistry.centralrepo.dto;

import java.io.Serializable;
import java.util.List;

public class CentralRepoDashboardResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private long pendingReviewCount;
    private long approvedCount;
    private long rejectedCount;
    private long totalCount;
    private List<CentralRepoListItemDTO> recentServices;

    public CentralRepoDashboardResponse() {
    }

    public CentralRepoDashboardResponse(long pendingReviewCount, long approvedCount, long rejectedCount, long totalCount, List<CentralRepoListItemDTO> recentServices) {
        this.pendingReviewCount = pendingReviewCount;
        this.approvedCount = approvedCount;
        this.rejectedCount = rejectedCount;
        this.totalCount = totalCount;
        this.recentServices = recentServices;
    }

    public long getPendingReviewCount() {
        return pendingReviewCount;
    }

    public void setPendingReviewCount(long pendingReviewCount) {
        this.pendingReviewCount = pendingReviewCount;
    }

    public long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(long approvedCount) {
        this.approvedCount = approvedCount;
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<CentralRepoListItemDTO> getRecentServices() {
        return recentServices;
    }

    public void setRecentServices(List<CentralRepoListItemDTO> recentServices) {
        this.recentServices = recentServices;
    }
}
