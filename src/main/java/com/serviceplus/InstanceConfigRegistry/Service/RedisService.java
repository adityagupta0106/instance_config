package com.serviceplus.InstanceConfigRegistry.Service;


import static com.serviceplus.InstanceConfigRegistry.util.Utility.entityToString;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.stringToEntity;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.stringToEntityUsingType;
import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.REDIS_KEY_EXIPRATION_DURATION_IN_MINUTES;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, String> template;
	
	public void add(Object obj,String key) {

		try {
			String finalJson = entityToString(obj);
			template.opsForValue().set(key, finalJson, Duration.ofMinutes(REDIS_KEY_EXIPRATION_DURATION_IN_MINUTES));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object fetch(String key, Class<?> clazz) {

		try {
			String json = template.opsForValue().get(key);			
			Object map = stringToEntity(json,clazz);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Object fetchUsingType(String key, Type typeOfT) {
	    try {
	        String json = template.opsForValue().get(key);
	        if (json != null) {
	            return stringToEntityUsingType(json, typeOfT);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	
	public void evict(String key) {
		try {
			template.opsForValue().getAndDelete(key);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void flushAll() {
		try (RedisConnection connection = template.getConnectionFactory().getConnection()) {
		    if (connection instanceof RedisServerCommands) {
		        ((RedisServerCommands) connection).flushDb();
		    }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteKeysWithPrefix(String prefix) {
		try {
			String pattern = prefix + "*";
			Set<String> keys = template.keys(pattern);

			if (keys != null && !keys.isEmpty()) {
				template.delete(keys);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
