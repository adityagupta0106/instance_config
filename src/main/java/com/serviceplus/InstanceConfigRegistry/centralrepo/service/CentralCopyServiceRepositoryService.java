package com.serviceplus.InstanceConfigRegistry.centralrepo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.UlidCreator;
import com.serviceplus.InstanceConfigRegistry.ApiClient.ServiceMetadataApiClient;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.centralrepo.dto.*;
import com.serviceplus.InstanceConfigRegistry.centralrepo.entity.CentralCopyServiceRepository;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoActionType;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoServiceStatus;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.ReviewAction;
import com.serviceplus.InstanceConfigRegistry.centralrepo.repository.CentralCopyServiceRepositoryRepository;
import com.serviceplus.InstanceConfigRegistry.dto.ServerSidePaginationRecord;
import com.serviceplus.InstanceConfigRegistry.dto.UserSessionObject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CentralCopyServiceRepositoryService {

    private static final Logger log = LogManager.getLogger(CentralCopyServiceRepositoryService.class);

    private final CentralCopyServiceRepositoryRepository repository;
    private final ServiceMetadataApiClient serviceMetadataApiClient;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public CentralCopyServiceRepositoryService(
            CentralCopyServiceRepositoryRepository repository,
            ServiceMetadataApiClient serviceMetadataApiClient,
            MongoTemplate mongoTemplate,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.serviceMetadataApiClient = serviceMetadataApiClient;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Push a service definition to Central Repository (initiated by Department Admin).
     * Enforces eligibility: status=LAUNCHED (4) and applications submitted >= 2.
     * Enforces duplicate check by serviceId only.
     */
    public CentralRepoActionResponse pushService(PushServiceRequest request, UserSessionObject user) {
        if (request == null || request.getServiceId() == null) {
            throw new SPRuntimeError("Service ID must be specified", HttpStatus.BAD_REQUEST);
        }

        Integer serviceId = request.getServiceId();
        log.info("Pushing service to Central Repository. serviceId={}", serviceId);

        validateEligibility(request.getServiceStatus(), request.getSubmittedApplicationsCount());

        // Enforce rule: No service can be pulled or pushed two times if APPROVED or PENDING_REVIEW
        Optional<CentralCopyServiceRepository> existingOpt = repository.findByServiceId(serviceId);
        if (existingOpt.isPresent()) {
            CentralCopyServiceRepository existing = existingOpt.get();
            if (existing.getStatus() == RepoServiceStatus.APPROVED) {
                throw new SPRuntimeError("Same service cannot be pushed/pulled twice. Service is already approved in Central Repository.", HttpStatus.BAD_REQUEST);
            }
            if (existing.getStatus() == RepoServiceStatus.PENDING_REVIEW) {
                throw new SPRuntimeError("Same service cannot be pushed/pulled twice. Service is already awaiting review in Central Repository.", HttpStatus.BAD_REQUEST);
            }
        }

        CentralCopyServiceRepository entity = existingOpt.orElseGet(() -> {
            CentralCopyServiceRepository newEntity = new CentralCopyServiceRepository();
            newEntity.setId(UlidCreator.getMonotonicUlid().toString());
            newEntity.setServiceId(serviceId);
            newEntity.setCreatedAt(new Date());
            return newEntity;
        });
        entity.setServiceName(request.getServiceName());
        entity.setServiceAbbreviation(request.getServiceAbbreviation());
        entity.setSourceTenantId(request.getSourceTenantId());
        entity.setTenantName(request.getTenantName());
        entity.setEntityId(request.getEntityId());
        entity.setEntityName(request.getEntityName());
        entity.setSourceDepartmentId(request.getEntityId());
        entity.setSourceDepartmentName(request.getEntityName());
        entity.setDepartmentName(request.getEntityName());
        entity.setVersionNo(request.getVersionNo() != null ? request.getVersionNo() : 1);
        entity.setMinorVersionNo(request.getMinorVersionNo() != null ? request.getMinorVersionNo() : 0);
        entity.setSubmittedApplicationsCount(request.getSubmittedApplicationsCount() != null ? request.getSubmittedApplicationsCount() : 0L);
        entity.setStatus(RepoServiceStatus.PENDING_REVIEW);
        entity.setActionType(RepoActionType.PUSH);
        entity.setPushedBy(user != null ? user.getUserID() : null);
        entity.setPushedByName(user != null ? user.getUserName() : "Department Admin");
        entity.setPushedDate(new Date());
        entity.setSlaDays(request.getSlaDays());
        entity.setRejectionReason(null);
        entity.setReviewedBy(null);
        entity.setReviewedByName(null);
        entity.setReviewedDate(null);
        entity.setUpdatedAt(new Date());

        CentralCopyServiceRepository saved = repository.save(entity);
        return mapToActionResponse(saved, "Service successfully pushed to Central Repository for Instance Admin review");
    }

    /**
     * Direct Pull of a service definition into Central Repository by Instance Admin.
     * Pulls record based on serviceId only; query by serviceId only.
     */
    public CentralRepoActionResponse pullService(PullServiceRequest request, UserSessionObject user, HttpServletRequest httpRequest) {
        if (request == null || request.getServiceId() == null) {
            throw new SPRuntimeError("Service ID must be specified for pulling", HttpStatus.BAD_REQUEST);
        }

        Integer serviceId = request.getServiceId();
        log.info("Direct pull initiated by Instance Admin for serviceId={}", serviceId);


        // Fetch service metadata from ServiceMetadata using serviceId
        ServicePullInfoDTO pullInfo = serviceMetadataApiClient.getServicePullInfo(serviceId, httpRequest);
        if (pullInfo == null) {
            throw new SPRuntimeError("Service metadata could not be retrieved for serviceId: " + serviceId, HttpStatus.NOT_FOUND);
        }

        // Validate eligibility: LAUNCHED status (4) and at least 2 submitted applications
        validateEligibility(pullInfo.getServiceStatus(), pullInfo.getSubmittedApplicationsCount());

        // Enforce rule: No service can be pulled or pushed two times if APPROVED or PENDING_REVIEW
        Optional<CentralCopyServiceRepository> existingOpt = repository.findByServiceId(serviceId);
        if (existingOpt.isPresent()) {
            CentralCopyServiceRepository existing = existingOpt.get();
            if (existing.getStatus() == RepoServiceStatus.APPROVED) {
                throw new SPRuntimeError("Same service cannot be pushed/pulled twice. Service is already approved in Central Repository.", HttpStatus.BAD_REQUEST);
            }
            if (existing.getStatus() == RepoServiceStatus.PENDING_REVIEW) {
                throw new SPRuntimeError("Same service cannot be pushed/pulled twice. Service is already awaiting review in Central Repository.", HttpStatus.BAD_REQUEST);
            }
        }

        CentralCopyServiceRepository entity = existingOpt.orElseGet(() -> {
            CentralCopyServiceRepository newEntity = new CentralCopyServiceRepository();
            newEntity.setId(UlidCreator.getMonotonicUlid().toString());
            newEntity.setServiceId(pullInfo.getServiceId());
            newEntity.setCreatedAt(new Date());
            return newEntity;
        });
        entity.setServiceName(pullInfo.getServiceName());
        entity.setServiceAbbreviation(pullInfo.getServiceAbbreviation());
        entity.setSourceTenantId(pullInfo.getSourceTenantId());
        entity.setSourceDepartmentId(pullInfo.getSourceDepartmentId());
        entity.setSourceDepartmentName(pullInfo.getSourceDepartmentName());
        entity.setVersionNo(pullInfo.getVersionNo() != null ? pullInfo.getVersionNo() : 1);
        entity.setMinorVersionNo(pullInfo.getMinorVersionNo() != null ? pullInfo.getMinorVersionNo() : 0);
        entity.setSubmittedApplicationsCount(pullInfo.getSubmittedApplicationsCount() != null ? pullInfo.getSubmittedApplicationsCount() : 0L);
        entity.setStatus(RepoServiceStatus.PENDING_REVIEW);
        entity.setActionType(RepoActionType.PULL);
        entity.setPushedBy(user != null ? user.getUserID() : null);
        entity.setPushedByName(user != null ? user.getUserName() : "Instance Admin (Direct Pull)");
        entity.setPushedDate(new Date());
        entity.setSlaDays(pullInfo.getSlaDays());
        entity.setRejectionReason(null);
        entity.setReviewedBy(null);
        entity.setReviewedByName(null);
        entity.setReviewedDate(null);
        entity.setUpdatedAt(new Date());

        CentralCopyServiceRepository saved = repository.save(entity);
        return mapToActionResponse(saved, "Service successfully pulled into Central Repository for review");
    }

    /**
     * Search & list services available to be pulled by Instance Admin from ServiceMetadata.
     * Uses server-side pagination with ServerSidePaginationRecord.
     */
    public ServerSidePaginationRecord<AvailableServicePullDTO> getAvailableServicesToPull(
            String search, int page, int size, HttpServletRequest httpRequest) {
        int safePage = Math.max(0, Math.min(page, 10000));
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        ServerSidePaginationRecord<AvailableServicePullDTO> record =
                serviceMetadataApiClient.getAvailableServicesToPull(search, safePage, safeSize, 2, httpRequest);

        // Enhance with Central Repository duplicate existence check by serviceId
        if (record != null && record.getData() != null) {
            for (Object itemObj : record.getData()) {
                AvailableServicePullDTO item = (itemObj instanceof AvailableServicePullDTO)
                        ? (AvailableServicePullDTO) itemObj
                        : objectMapper.convertValue(itemObj, AvailableServicePullDTO.class);
                item.setAlreadyInCentralRepo(checkExists(item.getServiceId()));
            }
        }
        return record;
    }

    /**
     * Search & list services available in Central Repository for Department Admin to copy.
     * Only APPROVED services are available for copy.
     * Categorized:
     * - SAME_DEPARTMENT: services where sourceDepartmentId == departmentId (or tenant's department)
     * - GLOBAL_SERVICES: global services across multiple tenants (all approved)
     * Uses server-side pagination with ServerSidePaginationRecord.
     */
    public ServerSidePaginationRecord<CentralRepoListItemDTO> getAvailableServicesToCopy(
            String category, Long departmentId, String search, int page, int size) {
        int safePage = Math.max(0, Math.min(page, 10000));
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);

        Query query = new Query();
        // Mandatory: Service must be approved in Central Repository to be copied
        query.addCriteria(Criteria.where("status").is(RepoServiceStatus.APPROVED));

        // Category filter: SAME_DEPARTMENT
        if ("SAME_DEPARTMENT".equalsIgnoreCase(category) || "DEPARTMENTAL".equalsIgnoreCase(category)) {
            if (departmentId != null) {
                query.addCriteria(Criteria.where("sourceDepartmentId").is(departmentId));
            }
        }

        // Search filter (serviceName or serviceAbbreviation case-insensitive)
        if (search != null && !search.isBlank()) {
            String regex = java.util.regex.Pattern.quote(search.trim());
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("serviceName").regex(regex, "i"),
                    Criteria.where("serviceAbbreviation").regex(regex, "i")
            ));
        }

        long total = mongoTemplate.count(query, CentralCopyServiceRepository.class);

        query.with(PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<CentralCopyServiceRepository> entities = mongoTemplate.find(query, CentralCopyServiceRepository.class);

        List<CentralRepoListItemDTO> dtoList = entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        int totalPages = safeSize > 0 ? (int) Math.ceil((double) total / safeSize) : 0;

        return new ServerSidePaginationRecord<>(
                dtoList,
                total,
                safePage,
                safeSize,
                totalPages
        );
    }

    /**
     * Central Instance Admin review: Approve (Publish to Catalogue) or Reject (with remarks).
     */
    public CentralRepoActionResponse reviewService(ReviewServiceRequest request, UserSessionObject user) {
        log.info("Reviewing central repo service id={}, action={}", request.getId(), request.getAction());

        CentralCopyServiceRepository entity = repository.findById(request.getId())
                .orElseThrow(() -> new SPRuntimeError("Service not found in Central Repository with ID: " + request.getId(), HttpStatus.NOT_FOUND));

        if (request.getAction() == ReviewAction.APPROVE) {
            entity.setStatus(RepoServiceStatus.APPROVED);
            entity.setRejectionReason(null);
        } else if (request.getAction() == ReviewAction.REJECT) {
            entity.setStatus(RepoServiceStatus.REJECTED);
            String reason = request.getRejectionReason();
            if (reason == null || reason.isBlank()) {
                throw new SPRuntimeError("Rejection reason is mandatory when rejecting a service", HttpStatus.BAD_REQUEST);
            }
            entity.setRejectionReason(reason.trim());
        } else {
            throw new SPRuntimeError("Invalid review action. Allowed actions are APPROVE or REJECT", HttpStatus.BAD_REQUEST);
        }

        entity.setReviewedBy(user != null ? user.getUserID() : null);
        entity.setReviewedByName(user != null ? user.getUserName() : "Instance Admin");
        entity.setReviewedDate(new Date());
        entity.setUpdatedAt(new Date());

        CentralCopyServiceRepository saved = repository.save(entity);
        String msg = (saved.getStatus() == RepoServiceStatus.APPROVED)
                ? "Service approved and published to Copy Service Catalogue"
                : "Service rejected by Instance Admin";
        return mapToActionResponse(saved, msg);
    }

    /**
     * Review History of reviewed services (APPROVED and REJECTED) with server-side pagination.
     */
    public ServerSidePaginationRecord<ReviewHistoryItemDTO> getReviewHistory(String search, int page, int size) {
        int safePage = Math.max(0, Math.min(page, 10000));
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "reviewedDate"));
        List<RepoServiceStatus> reviewedStatuses = List.of(RepoServiceStatus.APPROVED, RepoServiceStatus.REJECTED);

        Page<CentralCopyServiceRepository> entityPage;
        if (search != null && !search.isBlank()) {
            entityPage = repository.findByStatusInAndServiceNameContainingIgnoreCase(reviewedStatuses, search.trim(), pageable);
        } else {
            entityPage = repository.findByStatusIn(reviewedStatuses, pageable);
        }

        List<ReviewHistoryItemDTO> dtoList = entityPage.getContent().stream()
                .map(this::mapToReviewHistoryDTO)
                .collect(Collectors.toList());

        return new ServerSidePaginationRecord<>(
                dtoList,
                entityPage.getTotalElements(),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalPages()
        );
    }

    public CentralRepoDashboardResponse getDashboardSummary() {
        long pending = repository.countByStatus(RepoServiceStatus.PENDING_REVIEW);
        long approved = repository.countByStatus(RepoServiceStatus.APPROVED);
        long rejected = repository.countByStatus(RepoServiceStatus.REJECTED);
        long total = repository.count();

        List<CentralCopyServiceRepository> recentEntities = repository.findTop10ByOrderByCreatedAtDesc();
        List<CentralRepoListItemDTO> recentDtoList = recentEntities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new CentralRepoDashboardResponse(pending, approved, rejected, total, recentDtoList);
    }

    /**
     * List services maintained in Central Repository with server-side pagination.
     */
    public ServerSidePaginationRecord<CentralRepoListItemDTO> listServices(
            RepoServiceStatus status, String search, int page, int size) {
        int safePage = Math.max(0, Math.min(page, 10000));
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CentralCopyServiceRepository> entityPage;

        boolean hasStatus = (status != null);
        boolean hasSearch = (search != null && !search.isBlank());

        if (hasStatus && hasSearch) {
            entityPage = repository.findByStatusAndServiceNameContainingIgnoreCase(status, search.trim(), pageable);
        } else if (hasStatus) {
            entityPage = repository.findByStatus(status, pageable);
        } else if (hasSearch) {
            entityPage = repository.findByServiceNameContainingIgnoreCase(search.trim(), pageable);
        } else {
            entityPage = repository.findAll(pageable);
        }

        List<CentralRepoListItemDTO> dtoList = entityPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new ServerSidePaginationRecord<>(
                dtoList,
                entityPage.getTotalElements(),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalPages()
        );
    }

    /**
     * Get published catalogue services (Global Services for Service Definers).
     */
    public List<CentralRepoListItemDTO> getCatalogue(String search) {
        List<CentralCopyServiceRepository> list;
        if (search != null && !search.isBlank()) {
            list = repository.findByStatusAndServiceNameContainingIgnoreCaseOrderByCreatedAtDesc(
                    RepoServiceStatus.APPROVED, search.trim());
        } else {
            list = repository.findByStatusOrderByCreatedAtDesc(RepoServiceStatus.APPROVED);
        }

        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Get full details of a service in Central Repository.
     */
    public CentralCopyServiceRepository getServiceDetails(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new SPRuntimeError("Service not found in Central Repository with ID: " + id, HttpStatus.NOT_FOUND));
    }

    public CentralCopyServiceRepository getServiceDetailsByServiceId(Integer serviceId) {
        return repository.findByServiceId(serviceId)
                .orElseThrow(() -> new SPRuntimeError("Service not found in Central Repository with serviceId: " + serviceId, HttpStatus.NOT_FOUND));
    }

    /**
     * Check duplicate rule: Same service cannot be pushed/pulled twice (queried by serviceId only).
     */
    public boolean checkExists(Integer serviceId) {
        if (serviceId == null) {
            return false;
        }
        Optional<CentralCopyServiceRepository> repo = repository.findByServiceId(serviceId);
        return repo.map(centralCopyServiceRepository ->
                centralCopyServiceRepository.getStatus().equals(RepoServiceStatus.APPROVED)
                || centralCopyServiceRepository.getStatus().equals(RepoServiceStatus.PENDING_REVIEW)
        ).orElse(false);
    }

    /**
     * Validate whether a service in Central Repository has been reviewed and APPROVED by Instance Admin.
     */
    public boolean isApproved(String id, Integer serviceId) {
        if (id != null && !id.isBlank()) {
            return repository.existsByIdAndStatus(id, RepoServiceStatus.APPROVED);
        }
        if (serviceId != null) {
            return repository.existsByServiceIdAndStatus(serviceId, RepoServiceStatus.APPROVED);
        }
        return false;
    }

    private void validateEligibility(Integer serviceStatus, Long applicationCount) {
        if (serviceStatus == null || serviceStatus != 4) {
            throw new SPRuntimeError("Service is not eligible: Service must be in LAUNCHED status", HttpStatus.BAD_REQUEST);
        }
        if (applicationCount == null || applicationCount < 2) {
            throw new SPRuntimeError("Service is not eligible: At least 2 applications must have been submitted against the service", HttpStatus.BAD_REQUEST);
        }
    }

    public CentralRepoActionResponse mapToActionResponse(CentralCopyServiceRepository entity, String message) {
        CentralRepoActionResponse resp = new CentralRepoActionResponse();
        resp.setId(entity.getId());
        resp.setServiceId(entity.getServiceId());
        resp.setServiceName(entity.getServiceName());
        resp.setServiceAbbreviation(entity.getServiceAbbreviation());
        resp.setStatus(entity.getStatus());
        resp.setActionType(entity.getActionType());
        resp.setSourceDepartmentName(entity.getSourceDepartmentName());
        resp.setActionByName(entity.getActionType() == RepoActionType.PULL ? entity.getPushedByName() : entity.getReviewedByName());
        resp.setActionDate(entity.getReviewedDate() != null ? entity.getReviewedDate() : entity.getPushedDate());
        resp.setRejectionReason(entity.getRejectionReason());
        resp.setMessage(message);
        return resp;
    }

    public CentralRepoListItemDTO mapToDTO(CentralCopyServiceRepository entity) {
        CentralRepoListItemDTO dto = new CentralRepoListItemDTO();
        dto.setId(entity.getId());
        dto.setServiceId(entity.getServiceId());
        dto.setServiceName(entity.getServiceName());
        dto.setServiceAbbreviation(entity.getServiceAbbreviation());
        dto.setSourceTenantId(entity.getSourceTenantId());
        dto.setSourceTenantName(entity.getTenantName() != null ? entity.getTenantName() : entity.getSourceTenantId());
        dto.setSourceDepartmentId(entity.getSourceDepartmentId());
        dto.setSourceDepartmentName(entity.getDepartmentName() != null ? entity.getDepartmentName() : entity.getSourceDepartmentName());
        dto.setVersionNo(entity.getVersionNo());
        dto.setMinorVersionNo(entity.getMinorVersionNo());
        dto.setSubmittedApplicationsCount(entity.getSubmittedApplicationsCount());
        dto.setStatus(entity.getStatus());
        dto.setRejectionReason(entity.getRejectionReason());
        dto.setPushedByName(entity.getPushedByName());
        dto.setPushedDate(entity.getPushedDate());
        dto.setReviewedByName(entity.getReviewedByName());
        dto.setReviewedDate(entity.getReviewedDate());
        dto.setActionType(entity.getActionType());
        dto.setSlaDays(entity.getSlaDays());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setEntityId(entity.getEntityId() != null ? entity.getEntityId() : entity.getSourceDepartmentId());
        dto.setEntityName(entity.getEntityName());
        dto.setDepartmentName(entity.getDepartmentName() != null ? entity.getDepartmentName() : entity.getSourceDepartmentName());
        dto.setTenantName(entity.getTenantName() != null ? entity.getTenantName() : entity.getSourceTenantId());
        return dto;
    }

    public ReviewHistoryItemDTO mapToReviewHistoryDTO(CentralCopyServiceRepository entity) {
        ReviewHistoryItemDTO dto = new ReviewHistoryItemDTO();
        dto.setId(entity.getId());
        dto.setServiceId(entity.getServiceId());
        dto.setServiceName(entity.getServiceName());
        dto.setServiceAbbreviation(entity.getServiceAbbreviation());
        dto.setSourceTenantName(entity.getTenantName() != null ? entity.getTenantName() : entity.getSourceTenantId());
        dto.setSourceDepartmentName(entity.getDepartmentName() != null ? entity.getDepartmentName() : entity.getSourceDepartmentName());
        dto.setVersionNo(entity.getVersionNo());
        dto.setMinorVersionNo(entity.getMinorVersionNo());
        dto.setStatus(entity.getStatus());
        dto.setActionType(entity.getActionType());
        dto.setPushedByName(entity.getPushedByName());
        dto.setPushedDate(entity.getPushedDate());
        dto.setReviewedByName(entity.getReviewedByName());
        dto.setReviewedDate(entity.getReviewedDate());
        dto.setRejectionReason(entity.getRejectionReason());
        return dto;
    }
}
