package com.serviceplus.InstanceConfigRegistry.dto;

import java.util.List;

public class SaveConfigRequest {

	private List<Data> data;
	
	public static class Data{
		private String key;
		private Object value;
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
	}

	public List<Data> getData() {
		return data;
	}

	public void setData(List<Data> data) {
		this.data = data;
	}

	
}
