package com.serviceplus.InstanceConfigRegistry.centralrepo.repository;

import com.serviceplus.InstanceConfigRegistry.centralrepo.entity.CentralCopyServiceRepository;
import com.serviceplus.InstanceConfigRegistry.centralrepo.enums.RepoServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CentralCopyServiceRepositoryRepository extends MongoRepository<CentralCopyServiceRepository, String> {

    Optional<CentralCopyServiceRepository> findByServiceId(Integer serviceId);

    boolean existsByServiceId(Integer serviceId);

    boolean existsByServiceIdAndStatus(Integer serviceId, RepoServiceStatus status);

    boolean existsByIdAndStatus(String id, RepoServiceStatus status);

    // Optimized top 10 for dashboard without full collection scans
    List<CentralCopyServiceRepository> findTop10ByOrderByCreatedAtDesc();

    // Server-side paginated queries
    Page<CentralCopyServiceRepository> findByStatusIn(List<RepoServiceStatus> statuses, Pageable pageable);

    Page<CentralCopyServiceRepository> findByStatusInAndServiceNameContainingIgnoreCase(
            List<RepoServiceStatus> statuses, String serviceName, Pageable pageable);

    Page<CentralCopyServiceRepository> findByStatus(RepoServiceStatus status, Pageable pageable);

    Page<CentralCopyServiceRepository> findByStatusAndServiceNameContainingIgnoreCase(
            RepoServiceStatus status, String serviceName, Pageable pageable);

    Page<CentralCopyServiceRepository> findByServiceNameContainingIgnoreCase(String serviceName, Pageable pageable);

    List<CentralCopyServiceRepository> findByStatusOrderByCreatedAtDesc(RepoServiceStatus status);

    List<CentralCopyServiceRepository> findByStatusAndServiceNameContainingIgnoreCaseOrderByCreatedAtDesc(
            RepoServiceStatus status, String serviceName);

    long countByStatus(RepoServiceStatus status);
}
