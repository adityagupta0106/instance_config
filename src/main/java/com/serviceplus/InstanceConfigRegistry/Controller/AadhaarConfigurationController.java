package com.serviceplus.InstanceConfigRegistry.Controller;

import com.serviceplus.InstanceConfigRegistry.Entity.AadhaarPluginMethodMaster;
import com.serviceplus.InstanceConfigRegistry.Service.AadhaarConfigurationService;
import com.serviceplus.InstanceConfigRegistry.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.getUserSessionDetails;
import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/a")
public class AadhaarConfigurationController {

    private final AadhaarConfigurationService aadhaarConfigurationService;


    public AadhaarConfigurationController(AadhaarConfigurationService aadhaarConfigurationService) {
        this.aadhaarConfigurationService = aadhaarConfigurationService;
    }


    @PostMapping("/aua/save")
    public ResponseEntity<AadhaarConfigurationResponse> createConfiguration(HttpServletRequest httpRequest, @RequestBody AadhaarConfigurationRequest request) {

        UserSessionObject user = requireNonNull(getUserSessionDetails(httpRequest));
        AadhaarConfigurationResponse response = aadhaarConfigurationService.save(request,user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/aua/get")
    public ResponseEntity<AadhaarConfigurationResponse> getConfiguration(HttpServletRequest httpRequest, @RequestParam String configurationId) {

        UserSessionObject user = requireNonNull(getUserSessionDetails(httpRequest));
        AadhaarConfigurationResponse response = aadhaarConfigurationService.getById(configurationId,user);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/aua/list")
    public ResponseEntity<List<AUAListing>> getAllConfigurations(HttpServletRequest httpRequest) {

        UserSessionObject user = requireNonNull(getUserSessionDetails(httpRequest));
        List<AUAListing> response = aadhaarConfigurationService.getAll(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/aua/update-node")
    public ResponseEntity<AadhaarConfigurationResponse> updateNodeConfiguration(HttpServletRequest httpRequest, @RequestParam String configurationId,
                                                                                @RequestParam String nodeCode,
                                                                                @RequestBody AadhaarNodeConfigurationRequest request) {

        UserSessionObject user = requireNonNull(getUserSessionDetails(httpRequest));
        AadhaarConfigurationResponse response = aadhaarConfigurationService.updateNodeConfiguration(
                                                                            configurationId,
                                                                            nodeCode,
                                                                            request,
                                                                            user);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/aua/mapping-masters")
    public ResponseEntity<AadhaarMappingMasterResponse> getAadhaarMappingMasters(HttpServletRequest httpRequest) {

        UserSessionObject user = requireNonNull(getUserSessionDetails(httpRequest));
        AadhaarMappingMasterResponse response = aadhaarConfigurationService.getAadhaarMappingMasters(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aua/configurations")
    public ResponseEntity<List<MasterOptionResponse>> getAuaConfigurations(HttpServletRequest httpRequest) {

        UserSessionObject user = requireNonNull(getUserSessionDetails(httpRequest), "User session not found");
        return ResponseEntity.ok(aadhaarConfigurationService.getAuaConfigurations(user));
    }

    @GetMapping("/aua/definer-configuration")
    public ResponseEntity<AadhaarDefinerConfigurationResponse> getDefinerConfiguration(@RequestParam String configurationId, HttpServletRequest httpRequest) {

        UserSessionObject user = requireNonNull(getUserSessionDetails(httpRequest));

        AadhaarDefinerConfigurationResponse response = aadhaarConfigurationService.getDefinerConfiguration(configurationId, user);

        return ResponseEntity.ok(response);
    }
}