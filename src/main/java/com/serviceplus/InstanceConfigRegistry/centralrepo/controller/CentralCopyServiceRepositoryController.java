package com.serviceplus.InstanceConfigRegistry.centralrepo.controller;

import com.serviceplus.InstanceConfigRegistry.centralrepo.dto.*;
import com.serviceplus.InstanceConfigRegistry.centralrepo.entity.CentralCopyServiceRepository;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoServiceStatus;
import com.serviceplus.InstanceConfigRegistry.centralrepo.service.CentralCopyServiceRepositoryService;
import com.serviceplus.InstanceConfigRegistry.dto.ServerSidePaginationRecord;
import com.serviceplus.InstanceConfigRegistry.dto.UserSessionObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.getUserSessionDetails;

@RestController
@RequestMapping("/a")
public class CentralCopyServiceRepositoryController {

    private final CentralCopyServiceRepositoryService centralCopyService;

    public CentralCopyServiceRepositoryController(CentralCopyServiceRepositoryService centralCopyService) {
        this.centralCopyService = centralCopyService;
    }

    /**
     * Direct Pull of a service definition into Central Copy Service Repository by Instance Admin.
     * Pulls record based on serviceId only; query by serviceId only.
     * Enforces rule: Same service cannot be pushed/pulled twice.
     */
    @PostMapping("/central-copy-repo/pull")
    public ResponseEntity<CentralRepoActionResponse> pullService(
            @RequestBody PullServiceRequest request,
            HttpServletRequest httpRequest) {
        UserSessionObject user = getUserSessionDetails(httpRequest);
        CentralRepoActionResponse response = centralCopyService.pullService(request, user, httpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Instance Admin search API to get the list of services available to pull from ServiceMetadata.
     * Search by service name (case-insensitive ILIKE) with server-side pagination.
     */
    @GetMapping("/serviceCopyRepo/available-to-pull")
    public ResponseEntity<ServerSidePaginationRecord<AvailableServicePullDTO>> getAvailableServicesToPull(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        int safePage = Math.max(0, Math.min(page, 10000));
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        ServerSidePaginationRecord<AvailableServicePullDTO> result =
                centralCopyService.getAvailableServicesToPull(search, safePage, safeSize, httpRequest);
        return ResponseEntity.ok(result);
    }

    /**
     * Search & list services available in Central Repository for Department Admin to copy.
     * Only APPROVED services are returned.
     * Categorized:
     * - SAME_DEPARTMENT: services where sourceDepartmentId == departmentId
     * - GLOBAL_SERVICES: global services across multiple tenants
     * Uses server-side pagination with ServerSidePaginationRecord.
     */
    @GetMapping("/central-copy-repo/available-to-copy")
    public ResponseEntity<ServerSidePaginationRecord<CentralRepoListItemDTO>> getAvailableServicesToCopy(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(0, Math.min(page, 10000));
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        return ResponseEntity.ok(centralCopyService.getAvailableServicesToCopy(category, departmentId, search, safePage, safeSize));
    }

    /**
     * Central Instance Admin review: Approve (Publish to Catalogue) or Reject (with remarks).
     */
    @PostMapping("/serviceCopyRepo/review")
    public ResponseEntity<CentralRepoActionResponse> reviewService(
            @RequestBody ReviewServiceRequest request,
            HttpServletRequest httpRequest) {
        UserSessionObject user = getUserSessionDetails(httpRequest);
        CentralRepoActionResponse response = centralCopyService.reviewService(request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * History of reviewed services (APPROVED and REJECTED) with server-side pagination.
     */
    @GetMapping("/central-copy-repo/review-history")
    public ResponseEntity<ServerSidePaginationRecord<ReviewHistoryItemDTO>> getReviewHistory(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(0, Math.min(page, 10000));
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        ServerSidePaginationRecord<ReviewHistoryItemDTO> result =
                centralCopyService.getReviewHistory(search, safePage, safeSize);
        return ResponseEntity.ok(result);
    }

    /**
     * Central Repository Review Queue Dashboard for Instance Admin.
     * Optimized: avoids in-memory collection limits.
     */
    @GetMapping("/serviceCopyRepo/dashboard")
    public ResponseEntity<CentralRepoDashboardResponse> getDashboard() {
        return ResponseEntity.ok(centralCopyService.getDashboardSummary());
    }

    /**
     * List services maintained in Central Repository with server-side pagination.
     */
    @GetMapping("/serviceCopyRepo/list")
    public ResponseEntity<ServerSidePaginationRecord<CentralRepoListItemDTO>> listServices(
            @RequestParam(required = false) RepoServiceStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(0, Math.min(page, 10000));
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        return ResponseEntity.ok(centralCopyService.listServices(status, search, safePage, safeSize));
    }

    /**
     * Get published catalogue services (Global Services for Service Definers).
     */
    @GetMapping("/central-copy-repo/catalogue")
    public ResponseEntity<List<CentralRepoListItemDTO>> getCatalogue(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(centralCopyService.getCatalogue(search));
    }

    /**
     * Get full details of a service in Central Repository by id or serviceId.
     */
    @GetMapping("/central-copy-repo/details")
    public ResponseEntity<CentralCopyServiceRepository> getServiceDetails(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) Integer serviceId) {
        if (id != null && !id.isBlank()) {
            return ResponseEntity.ok(centralCopyService.getServiceDetails(id));
        } else if (serviceId != null) {
            return ResponseEntity.ok(centralCopyService.getServiceDetailsByServiceId(serviceId));
        }
        throw new com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError("Either id or serviceId must be provided", org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    /**
     * Check duplicate rule: Same service cannot be pushed/pulled twice (by serviceId).
     */
    @GetMapping("/central-copy-repo/check-exists")
    public ResponseEntity<Map<String, Object>> checkExists(
            @RequestParam Integer serviceId) {
        boolean exists = centralCopyService.checkExists(serviceId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("exists", exists);
        return ResponseEntity.ok(resp);
    }
}
