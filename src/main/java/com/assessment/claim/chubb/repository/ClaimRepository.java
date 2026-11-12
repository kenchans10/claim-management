package com.assessment.claim.chubb.repository;

import com.assessment.claim.chubb.common.enums.ClaimStatus;
import com.assessment.claim.chubb.entities.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByClaimantId(UUID claimantId);
    List<Claim> findByStatus(ClaimStatus status);
    Optional<Claim> findByClaimNumber(String claimNumber);
}