package com.serviceplus.InstanceConfigRegistry.dto;

import java.util.ArrayList;
import java.util.List;

public class AadhaarPayloadConfigurationResponse {

    private String xsd;

    private String schemaName;

    private List<AadhaarNodeConfigurationResponse> nodes =
            new ArrayList<>();


    public AadhaarPayloadConfigurationResponse() {
    }


    public String getXsd() {
        return xsd;
    }

    public void setXsd(
            String xsd
    ) {
        this.xsd = xsd;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(
            String schemaName
    ) {
        this.schemaName = schemaName;
    }

    public List<AadhaarNodeConfigurationResponse> getNodes() {
        return nodes;
    }

    public void setNodes(
            List<AadhaarNodeConfigurationResponse> nodes
    ) {
        this.nodes = nodes;
    }
}