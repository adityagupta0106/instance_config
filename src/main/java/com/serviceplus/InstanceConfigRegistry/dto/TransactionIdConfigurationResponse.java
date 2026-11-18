package com.serviceplus.InstanceConfigRegistry.dto;

public class TransactionIdConfigurationResponse {

    private String prefix;

    private String suffix;


    public TransactionIdConfigurationResponse() {
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(
            String prefix
    ) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(
            String suffix
    ) {
        this.suffix = suffix;
    }
}