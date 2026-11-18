package com.serviceplus.InstanceConfigRegistry.enums;

public enum AadhaarSystemGeneratedType {

    GENERATE_TXN("Generates a unique transaction ID"),
    CURRENT_TIMESTAMP("Generates the current timestamp"),
    SIGN("Creates the HMAC, Data, and Skey values required for the request.");

    private final String description;

    AadhaarSystemGeneratedType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
