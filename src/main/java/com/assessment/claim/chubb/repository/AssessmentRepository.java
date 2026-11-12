package com.assessment.claim.chubb.repository;

import com.assessment.claim.chubb.entities.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    List<Assessment> findByClaimId(UUID claimId);
    List<Assessment> findByStaffId(UUID staffId);
    Optional<Assessment> findFirstByClaimIdOrderByCreatedAtDesc(UUID claimId);
}
