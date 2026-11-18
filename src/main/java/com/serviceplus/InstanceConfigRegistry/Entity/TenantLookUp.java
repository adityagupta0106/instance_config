package com.serviceplus.InstanceConfigRegistry.Entity;

import java.io.Serializable;

import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("TenantLookUp")
public class TenantLookUp  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String domain;
	
	private String tenantId;

    private Boolean active;

	public TenantLookUp() {
	}

    public TenantLookUp(String domain, Boolean active, String tenantId) {
        this.domain = domain;
        this.active = active;
        this.tenantId = tenantId;
    }

    public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
