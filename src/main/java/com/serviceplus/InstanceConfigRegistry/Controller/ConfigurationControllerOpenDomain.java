package com.serviceplus.InstanceConfigRegistry.Controller;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.isEmpty;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.InstanceConfigRegistry.Entity.Configuration;
import com.serviceplus.InstanceConfigRegistry.Service.ConfigurationService;
import com.serviceplus.InstanceConfigRegistry.Service.TenantResolver;
import com.serviceplus.InstanceConfigRegistry.dto.TenantDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/o")
public class ConfigurationControllerOpenDomain {

	    @Autowired
	    private ConfigurationService configurationService;
	    
	    @Autowired
	    private TenantResolver tenantResolver;

	    @GetMapping("all")
	    public Configuration getConfig(HttpServletRequest request) {
	        return configurationService.getConfiguration(tenantResolver.get(request),request).orElse(null);
	    }
        //Here need to mask the sensitive feels
	    @PostMapping("specific")
	    public Configuration getConfigValues(@RequestParam(required = false) String tenantId, @RequestParam List<String> keys, HttpServletRequest request) {	        
	    	tenantId = isEmpty(tenantId) ? tenantResolver.get(request) : tenantId;
            Configuration configValues = configurationService.getConfigValues(tenantId, keys, request);
            //Map<String, Object> data = configValues.getData();

            //data.remove("JANPARICHAY");

            //configValues.setData(data);
            return configValues;
        }
	    
	    @GetMapping("tenantList")
	    public List<TenantDTO> getAllTenant() {
	    	return configurationService.getAllTenant();
	    }
	}
