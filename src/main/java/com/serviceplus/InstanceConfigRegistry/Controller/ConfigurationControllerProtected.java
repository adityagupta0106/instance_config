package com.serviceplus.InstanceConfigRegistry.Controller;

import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.DEFAULT_TENANT_ID;
import static com.serviceplus.InstanceConfigRegistry.util.Utility.isEmpty;

import java.util.List;
import java.util.Map;

import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.Service.TenantResolver;
import com.serviceplus.InstanceConfigRegistry.dto.TenantResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.InstanceConfigRegistry.Entity.Configuration;
import com.serviceplus.InstanceConfigRegistry.Entity.UserTenantMapping;
import com.serviceplus.InstanceConfigRegistry.Service.ConfigurationService;
import com.serviceplus.InstanceConfigRegistry.dto.SaveConfigRequest;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/a")
public class ConfigurationControllerProtected {

    private final ConfigurationService configurationService;

    private final TenantResolver tenantResolver;

    public ConfigurationControllerProtected(ConfigurationService configurationService, TenantResolver tenantResolver) {
        this.configurationService = configurationService;
        this.tenantResolver = tenantResolver;
    }

    @GetMapping("getRegisteredTenants")
    public List<UserTenantMapping> getRegisteredTenants(HttpServletRequest request) {        
        return configurationService.getRegisteredTenants(request);
    }
    
    @PostMapping("save")
    public Map<String,Object> updateConfigValue(@RequestParam(required=false) String tenantId,@RequestBody SaveConfigRequest dto,HttpServletRequest request) throws Exception {
    	tenantId = isEmpty(tenantId) ? DEFAULT_TENANT_ID : tenantId;
        return configurationService.updateConfigValue(tenantId, dto.getData(),request);
    }

    @PostMapping("saveCurrentTenant")
    public Map<String,Object> saveCurrentTenant(@RequestBody SaveConfigRequest dto,HttpServletRequest request) throws Exception {
        String tenantId = tenantResolver.get(request);
        if(isEmpty(tenantId)){
            throw new SPRuntimeError("Invalid request", HttpStatus.UNAUTHORIZED);
        }
        return configurationService.updateConfigValue(tenantId, dto.getData(),request);
    }
    
    @PostMapping("find")
    public Configuration getConfig(@RequestParam(required=false) String tenantId, HttpServletRequest request) {
    	tenantId = isEmpty(tenantId) ? DEFAULT_TENANT_ID : tenantId;
        return configurationService.getConfiguration(tenantId,request).orElse(null);
    }

    @PostMapping("currentTenant")
    public Configuration getCurrentTenant(HttpServletRequest request) {
        String tenantId = tenantResolver.get(request);
        if(isEmpty(tenantId)){
            throw new SPRuntimeError("Invalid request", HttpStatus.UNAUTHORIZED);
        }
        return configurationService.getConfiguration(tenantId,request).orElse(null);
    }

    @GetMapping("master")
    public Map<String, Object> fetchFromMaster(@RequestParam List<String> key) {
        return configurationService.fetchFromMaster(key);
    }

    @PostMapping("delete")
    public Map<String,String>  fetchFromMaster(@RequestParam(required=true) String tenantId, HttpServletRequest request) {
        return configurationService.deleteTenantConfiguration(tenantId,request);
    }

   // @PostMapping("updateMaster")
    public Map<String,String>  updateMaster(@RequestBody SaveConfigRequest dto,HttpServletRequest request) {
        return configurationService.updateMaster(dto,request);
    }

    @GetMapping("/tenant/config")
    public List<TenantResponse> getMyTenants(HttpServletRequest request) {
        return configurationService.getMyTenants(request);
    }
}
