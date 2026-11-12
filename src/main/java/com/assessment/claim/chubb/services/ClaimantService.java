package com.assessment.claim.chubb.services;

import com.assessment.claim.chubb.common.exceptions.ResourceNotFoundException;
import com.assessment.claim.chubb.dto.CreateClaimantRequest;
import com.assessment.claim.chubb.entities.Claimant;
import com.assessment.claim.chubb.repository.ClaimantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
/** Coordinates claimant profile persistence and lookup operations. */
public class ClaimantService {

    private final ClaimantRepository claimantRepository;

    public Claimant createClaimant(CreateClaimantRequest request) {
        log.info("Creating claimant with member number {}", request.claimantMemberNumber());
        Claimant claimant = new Claimant();
        claimant.setClaimantMemberNumber(request.claimantMemberNumber());
        claimant.setFirstName(request.firstName());
        claimant.setLastName(request.lastName());
        claimant.setEmail(request.email());
        claimant.setPhone(request.phone());
        claimant.setAddress(request.address());
        claimant.setPolicyNumber(request.policyNumber());
        return claimantRepository.save(claimant);
    }

    public Claimant getClaimant(UUID id) {
        return claimantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claimant not found: " + id));
    }

    public Claimant getClaimantByMemberNumber(String claimantMemberNumber) {
        return claimantRepository.findByClaimantMemberNumber(claimantMemberNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claimant not found: " + claimantMemberNumber));
    }
}