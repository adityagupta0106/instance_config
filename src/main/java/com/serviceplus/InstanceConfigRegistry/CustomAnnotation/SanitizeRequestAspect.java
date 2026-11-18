package com.serviceplus.InstanceConfigRegistry.CustomAnnotation;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import static com.serviceplus.InstanceConfigRegistry.util.GeneralValidation.isXss;

@Component
@Aspect
public class SanitizeRequestAspect {

	@Before("within(@org.springframework.web.bind.annotation.RestController *)")
    public void validateInput(JoinPoint joinPoint) throws Throwable {
    	
        for (Object arg : joinPoint.getArgs()) {
            if (arg == null)
            	continue;

            inspect(arg);
            
        }
    }

    private void inspect(Object obj) throws Exception {
        if (obj instanceof String) {
            checkString((String) obj, null);
        } 
        
        else if (obj instanceof Collection<?>) {
            for (Object item : (Collection<?>) obj) {
                inspect(item);
            }
        } 
        
        else if (obj instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                inspect(entry.getKey());
                inspect(entry.getValue());
            }
        } 
        
        else if (isUserDefinedObject(obj)) {
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.getType() != String.class) continue;

                field.setAccessible(true);
                String value = (String) field.get(obj);
                if (value != null) {
                    checkString(value, field.getName());
                }
            }
        }
    }

    private void checkString(String value, String fieldName) {
        
        if (isXss(value)) {
            throw new SPRuntimeError("Malicious request detected", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String lower = value.toLowerCase();
        String[] sqliPatterns = {
            "select", "insert", "update", "delete", "drop", "--", ";--",
            "' or '1'='1", "\" or \"1\"=\"1", " or 1=1", "union", "exec", "truncate",
            "char(", "cast(", "convert(","add"
        };

        for (String pattern : sqliPatterns) {
            if (lower.contains(pattern)) {
            	//throw new SPRuntimeError("Malicious request detected", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
    }

    private boolean isUserDefinedObject(Object obj) {
        String pkg = obj.getClass().getPackageName();
        return pkg.startsWith("com.serviceplus");
    }
}
