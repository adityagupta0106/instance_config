package com.serviceplus.InstanceConfigRegistry.util;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serviceplus.InstanceConfigRegistry.dto.SaveConfigRequest;
import com.serviceplus.InstanceConfigRegistry.dto.UserSessionObject;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.BASIC_TAB;

@Component
public class Utility {

    @Autowired
    private ObjectMapper objectMapperRef;

    @Autowired
    private ResourceLoader resourceLoaderRef;

    private static ObjectMapper objectMapper ;

    private static ResourceLoader resourceLoader;

    @PostConstruct
    private void init(){
        objectMapper = objectMapperRef;
        resourceLoader = resourceLoaderRef;
    }

    public static String entityToStringDisableHtmlEscape(Object data) {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(data);
    }

    public static String entityToString(Object data) {
        return new Gson().toJson(data);
    }

	public static <T> T stringToEntity(String data, Class<T> clazz)
			throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(data, clazz);
	}

	public static Object stringToEntityUsingType(String data, Type typeOfT) {
		return new Gson().fromJson(data, typeOfT);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getNodesData(List<String> keys, Map<String, Object> configData) {

		Map<String,Object> refValues = new HashMap<>();
		
		for(String key: keys) {
			try {
				Map<String,Object> currentMap = deepCopy(configData);
				String[] ref = key.split("\\.");
				Object value = null;
				for(String s : ref) {
					
					value = currentMap.get(s);
					
					if(value instanceof Map) {
						currentMap = (Map<String, Object>) value;
					}
				}
				
				refValues.put(key, value);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return refValues;
	}

    @SuppressWarnings("unchecked")
    public static void saveNode(List<SaveConfigRequest.Data> data, Map<String, Object> rootMap, Map<String, Object> master, boolean isInstanceAdmin) {

        Set<String> masterKeySet = masterJsonKeySet("", master, new HashSet<>());

        for (SaveConfigRequest.Data d : data) {

            String key = d.getKey();
            Object value = d.getValue();

            if (!masterKeySet.contains(key)) {
                continue;
            }
            String rootSection = key.split("\\.")[0];

            if (isInstanceAdmin) {
                if (!BASIC_TAB.equals(rootSection)) {
                    continue;
                }
            }
            else {
                if (BASIC_TAB.equals(rootSection)) {
                  //  continue;
                }
            }

            String[] path = key.split("\\.");

            Map<String, Object> currentMap = rootMap;

            for (int i = 0; i < path.length - 1; i++) {

                String nodeName = path[i];
                Object nextNode = currentMap.get(nodeName);

                if (!(nextNode instanceof Map)) {

                    Map<String, Object> newChild = new HashMap<>();
                    currentMap.put(nodeName, newChild);
                    currentMap = newChild;

                } else {
                    currentMap = (Map<String, Object>) nextNode;
                }
            }

            currentMap.put(path[path.length - 1], value);
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<String> masterJsonKeySet(String prefix, Map<String, Object> map, Set<String> result) {

        for (Map.Entry<String, Object> entry : map.entrySet()) {

            String newKey = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                masterJsonKeySet(newKey, (Map<String, Object>) value, result);

            } else {

                result.add(newKey);
            }
        }

        return result;
    }


    //EXCULDE ALL REFRENCES OF INNER MAPS
    @SuppressWarnings("unchecked")
    public static Map<String, Object> deepCopy(Map<String, Object> original) {
        Map<String, Object> copy = new HashMap<>();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                value = deepCopy((Map<String, Object>) value);
            }
            copy.put(entry.getKey(), value);
        }
        return copy;
    }
    
    public static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}
    
    /**
	 * Fetch logged in user details
	 * @param request
	 * @return UserSessionObject
	 */
	public static UserSessionObject getUserSessionDetails(HttpServletRequest request) {
		String user = request.getHeader("USER-DETAILS");
        System.out.println(user);
		if(isEmpty(user))
			return null;
			
		return (UserSessionObject) entityToStringGson(user,UserSessionObject.class);
	}
	
	public static Object entityToStringGson(String data,Class<?> classs) {
		return new Gson().fromJson(data, classs);
	}

    public static void filterDuplicatesInRootMap(Map<String, Object> rootMap) {
        for (Map.Entry<String, Object> entry : rootMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List<?>) {
                List<Object> uniqueList = new ArrayList<>(new LinkedHashSet<>((List<?>) value));
                entry.setValue(uniqueList);
            }
        }
    }

    public static String extractServiceName(String url) {
        if (url != null && url.startsWith("lb://")) {
            int firstSlash = url.indexOf('/', 5);
            if (firstSlash != -1) {
                return url.substring(5, firstSlash);
            }
            return url.substring(5);
        }
        return "unknown-service";
    }

    public static String generateUniqueToken(String PREFIX){
        return PREFIX
                + UlidCreator.getMonotonicUlid()
                .toString();
    }


    public static List<String> splitString(String domains) {

        if (isEmpty(domains)) {
            return Collections.emptyList();
        }

        return Arrays.stream(domains.split(","))
                .map(String::trim)
                .filter(domain -> !domain.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

}
