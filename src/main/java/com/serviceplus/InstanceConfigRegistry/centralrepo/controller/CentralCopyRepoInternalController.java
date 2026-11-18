package com.serviceplus.InstanceConfigRegistry.centralrepo.controller;

import com.serviceplus.InstanceConfigRegistry.centralrepo.dto.CentralRepoActionResponse;
import com.serviceplus.InstanceConfigRegistry.centralrepo.dto.PushServiceRequest;
import com.serviceplus.InstanceConfigRegistry.centralrepo.service.CentralCopyServiceRepositoryService;
import com.serviceplus.InstanceConfigRegistry.dto.UserSessionObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.getUserSessionDetails;

@RestController
@RequestMapping("/b")
public class CentralCopyRepoInternalController {

    private final CentralCopyServiceRepositoryService centralCopyService;

    public CentralCopyRepoInternalController(CentralCopyServiceRepositoryService centralCopyService) {
        this.centralCopyService = centralCopyService;
    }

    /**
     * Backend-to-backend endpoint: Validate whether a service has been reviewed and APPROVED by Instance Admin.
     */
    @GetMapping("/central-copy-repo/is-approved")
    public ResponseEntity<Map<String, Object>> isApproved(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) Integer serviceId) {
        boolean approved = centralCopyService.isApproved(id, serviceId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("approved", approved);
        if (serviceId != null) {
            resp.put("serviceId", serviceId);
        }
        if (id != null) {
            resp.put("id", id);
        }
        return ResponseEntity.ok(resp);
    }


    /**
     * Push a service definition to Central Copy Service Repository (initiated by Department Admin).
     * Enforces eligibility: LAUNCHED status, at least 2 submitted applications.
     * Enforces rule: Same service cannot be pushed/pulled twice (by serviceId).
     */
    @PostMapping("/central-copy-repo/push")
    public ResponseEntity<CentralRepoActionResponse> pushService(
            @RequestBody PushServiceRequest request,
            HttpServletRequest httpRequest) {
        UserSessionObject user = getUserSessionDetails(httpRequest);
        CentralRepoActionResponse response = centralCopyService.pushService(request, user);
        return ResponseEntity.ok(response);
    }
}
