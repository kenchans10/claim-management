package com.assessment.claim.chubb.services;

import com.assessment.claim.chubb.common.enums.ClaimStatus;
import com.assessment.claim.chubb.common.exceptions.BadRequestException;
import com.assessment.claim.chubb.common.exceptions.ResourceNotFoundException;
import com.assessment.claim.chubb.dto.ClaimResponse;
import com.assessment.claim.chubb.entities.Claim;
import com.assessment.claim.chubb.repository.ClaimRepository;
import com.assessment.claim.chubb.repository.StaffRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/** Applies validated claim lifecycle transitions. */
public class ClaimLifecycleService {

    private final ClaimRepository claimRepository;
    private final StaffRepository staffRepository;

    public ClaimLifecycleService(ClaimRepository claimRepository, StaffRepository staffRepository) {
        this.claimRepository = claimRepository;
        this.staffRepository = staffRepository;
    }

    public ClaimResponse assignClaim(String claimNumber, String staffNumber) {
        log.info("Assigning claim {} to staff {}", claimNumber, staffNumber);
        staffRepository.findByStaffNumber(staffNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));

        if (!isAssignable(claim.getStatus())) {
            throw new BadRequestException("Claim cannot be assigned in status: " + claim.getStatus());
        }

        claim.setStatus(ClaimStatus.ASSIGNED);
        Claim saved = claimRepository.save(claim);
        return new ClaimResponse(
                saved.getClaimNumber(),
                saved.getClaimant().getClaimantMemberNumber(),
                saved.getClaimType(),
                saved.getStatus(),
                saved.getDescription(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());
    }

    private boolean isAssignable(ClaimStatus status) {
        return status == ClaimStatus.SUBMITTED || status == ClaimStatus.MORE_INFO_REQUESTED;
    }
}
