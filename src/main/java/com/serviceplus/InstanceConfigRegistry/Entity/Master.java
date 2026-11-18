package com.serviceplus.InstanceConfigRegistry.Entity;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.JsonNode;

@Document("Master")
public class Master implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 370080656576532092L;
	@Id
	private Integer id;
    private Map<String, Object> data;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
