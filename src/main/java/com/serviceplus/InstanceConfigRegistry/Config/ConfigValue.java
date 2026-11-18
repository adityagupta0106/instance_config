package com.serviceplus.InstanceConfigRegistry.Config;

import java.io.Serializable;


    
public class ConfigValue implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String key;
    private final String value;

    public ConfigValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
