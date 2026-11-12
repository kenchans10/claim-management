package com.assessment.claim.chubb.repository;

import com.assessment.claim.chubb.common.enums.QueueStatus;
import com.assessment.claim.chubb.entities.StaffClaimQueue;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffClaimQueueRepository extends JpaRepository<StaffClaimQueue, UUID> {
    Optional<StaffClaimQueue> findByClaimId(UUID claimId);

    List<StaffClaimQueue> findByStatusOrderByQueuedAtAsc(QueueStatus status);

    List<StaffClaimQueue> findByStaffIdAndStatusOrderByPickedUpAtAsc(UUID staffId, QueueStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select queue from StaffClaimQueue queue where queue.claim.id = :claimId and queue.status = :status")
    Optional<StaffClaimQueue> findByClaimIdAndStatusForUpdate(
            @Param("claimId") UUID claimId,
            @Param("status") QueueStatus status);
}