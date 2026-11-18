package com.serviceplus.InstanceConfigRegistry.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class HolidayRequest {

    private String date;
    private String description;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonAnySetter
    public void rejectUnknownField(String name, Object value) {
        throw new IllegalArgumentException("Only date and description are allowed");
    }
}
