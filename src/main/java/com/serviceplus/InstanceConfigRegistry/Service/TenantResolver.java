package com.serviceplus.InstanceConfigRegistry.Service;

import static com.serviceplus.InstanceConfigRegistry.util.ApplicationConstants.HOST_HEADER;
import static java.util.Objects.nonNull;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplus.InstanceConfigRegistry.Entity.TenantLookUp;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.Repository.TenantLookupRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TenantResolver {
	//STORE TENANT ID IN USER DETAILS POST LOGIN
	@Autowired
	private TenantLookupRepository tenantLookupRepository;
	
	private static final Logger REGISTRY_LOGGER = LogManager.getLogger("RegistryLogger");
	
	
	/*
	 * HTTP,HTTPS not considered since from both protocol
	 * Configurations are allowed
	 * */
	
	public String get(HttpServletRequest request) {
		try {
			String host = request.getHeader(HOST_HEADER);
			REGISTRY_LOGGER.info("Host for request is {} ",host);
			if(nonNull(host)) {
				Optional<TenantLookUp> oLookUp = findTenant(host);
				if(oLookUp.isPresent()) {
					return oLookUp.get().getTenantId();
				}
				else {
					host = host.endsWith("/") ? host.substring(0, host.length() -1 ) : host.concat("/") ;
					oLookUp = findTenant(host);
					if(oLookUp.isPresent()) {
						return oLookUp.get().getTenantId();
					}
				}
			}
			
			return "";
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError();
		}
	}
	
	public void add(String host, String tenantId, Boolean active) {
		try {			
			Optional<TenantLookUp> tenant = findTenant(host);
			if(tenant.isEmpty()) {
				TenantLookUp lookup  = new TenantLookUp(host,active,tenantId);
				tenantLookupRepository.save(lookup);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError();
		}
	}
	
	public Optional<TenantLookUp> findTenant(String host) {
        return tenantLookupRepository.findById(host);
	}
	
}
