package com.serviceplus.InstanceConfigRegistry.centralrepo.dto;

import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.ReviewAction;

import java.io.Serializable;

public class ReviewServiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private ReviewAction action; // APPROVE or REJECT
    private String rejectionReason;

    public ReviewServiceRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReviewAction getAction() {
        return action;
    }

    public void setAction(ReviewAction action) {
        this.action = action;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
