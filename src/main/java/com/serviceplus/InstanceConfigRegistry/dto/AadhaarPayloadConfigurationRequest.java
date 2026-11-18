package com.serviceplus.InstanceConfigRegistry.dto;


import java.util.ArrayList;
import java.util.List;

public class AadhaarPayloadConfigurationRequest {

    private String xsd;

    private String schemaName;

    private List<AadhaarNodeConfigurationRequest> nodes =
            new ArrayList<>();


    public AadhaarPayloadConfigurationRequest() {
    }


    public String getXsd() {
        return xsd;
    }

    public void setXsd(String xsd) {
        this.xsd = xsd;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public List<AadhaarNodeConfigurationRequest> getNodes() {
        return nodes;
    }

    public void setNodes(
            List<AadhaarNodeConfigurationRequest> nodes
    ) {
        this.nodes = nodes;
    }
}
