package com.serviceplus.InstanceConfigRegistry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterOptionResponse {
    private String value;
    private String label;
    private String description;

    public MasterOptionResponse() {
    }

    public MasterOptionResponse(String code, String label) {
        this.value = code;
        this.label = label;
    }

    public MasterOptionResponse(String value, String label, String description) {
        this.value = value;
        this.label = label;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
