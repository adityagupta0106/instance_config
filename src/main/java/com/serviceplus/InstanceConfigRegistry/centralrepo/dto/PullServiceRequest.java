package com.serviceplus.InstanceConfigRegistry.centralrepo.dto;

import java.io.Serializable;

public class PullServiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer serviceId;

    public PullServiceRequest() {
    }

    public PullServiceRequest(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }
}
