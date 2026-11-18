package com.serviceplus.InstanceConfigRegistry.Controller;

import com.serviceplus.InstanceConfigRegistry.Entity.Configuration;
import com.serviceplus.InstanceConfigRegistry.Service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.serviceplus.InstanceConfigRegistry.Service.TenantResolver;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.isEmpty;

@RestController
@RequestMapping("/b")
public class PrivateApiController {

    private final ConfigurationService configurationService;

    private final TenantResolver tenantResolver;

    public PrivateApiController(ConfigurationService configurationService, TenantResolver tenantResolver) {
        this.configurationService = configurationService;
        this.tenantResolver = tenantResolver;
    }
	
	@GetMapping("fetchTenantId")
    public String getRegisteredTenants(HttpServletRequest request) {        
        return tenantResolver.get(request);
    }

    @PostMapping("internal-specific")
    public Configuration getConfigValues(@RequestParam(required = false) String tenantId, @RequestParam List<String> keys, HttpServletRequest request) {
        tenantId = isEmpty(tenantId) ? tenantResolver.get(request) : tenantId;
        return configurationService.getConfigValues(tenantId, keys, request);
    }
}
