package com.assessment.claim.chubb.repository;

import com.assessment.claim.chubb.entities.Claimant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimantRepository extends JpaRepository<Claimant, UUID> {
    Optional<Claimant> findByClaimantMemberNumber(String claimantMemberNumber);
}