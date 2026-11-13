package com.assessment.claim.chubb.services;

import com.assessment.claim.chubb.common.enums.ClaimStatus;
import com.assessment.claim.chubb.common.exceptions.BadRequestException;
import com.assessment.claim.chubb.common.exceptions.ResourceNotFoundException;
import com.assessment.claim.chubb.dto.ClaimReadyForStaffEvent;
import com.assessment.claim.chubb.dto.ClaimResponse;
import com.assessment.claim.chubb.dto.CreateClaimRequest;
import com.assessment.claim.chubb.dto.UpdateClaimInformationRequest;
import com.assessment.claim.chubb.entities.Claim;
import com.assessment.claim.chubb.entities.Claimant;
import com.assessment.claim.chubb.repository.ClaimRepository;
import com.assessment.claim.chubb.repository.ClaimantRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
/** Coordinates claim persistence and claim-related workflow events. */
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimantRepository claimantRepository;
    private final NotificationService notificationService;
    private final StaffClaimQueueService staffClaimQueueService;

    public ClaimService(ClaimRepository claimRepository, ClaimantRepository claimantRepository,
                        NotificationService notificationService,
                        StaffClaimQueueService staffClaimQueueService) {
        this.claimRepository = claimRepository;
        this.claimantRepository = claimantRepository;
        this.notificationService = notificationService;
        this.staffClaimQueueService = staffClaimQueueService;
    }

    @Transactional
    /**
     * Creates a submitted claim and records its staff-queue event transactionally.
     */
    public ClaimResponse createClaim(CreateClaimRequest request) {
        log.info("Creating claim for claimant member {}", request.claimantMemberNumber());
        Claimant claimant = claimantRepository.findByClaimantMemberNumber(request.claimantMemberNumber())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Claimant not found: " + request.claimantMemberNumber()));

        Claim claim = new Claim();
        claim.setClaimant(claimant);
        claim.setClaimType(request.claimType());
        claim.setDescription(request.description());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setClaimNumber(generateClaimNumber());
        claim.setPolicyNumber(claimant.getPolicyNumber());

        Claim saved = claimRepository.save(claim);
        notificationService.publishClaimReadyForStaff(new ClaimReadyForStaffEvent(
                saved.getId(), saved.getClaimNumber()));
        return toResponse(saved);
    }

    public ClaimResponse getClaim(UUID claimId) {
        return claimRepository.findById(claimId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));
    }

    public ClaimResponse getClaimByNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));
    }

    @Transactional
    /** Updates claimant information and reopens the existing staff queue entry. */
    public ClaimResponse updateClaimInformation(String claimNumber, UpdateClaimInformationRequest request) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));

        if (claim.getStatus() != ClaimStatus.MORE_INFO_REQUESTED) {
            throw new BadRequestException("Additional information cannot be provided in status: " + claim.getStatus());
        }

        claim.setDescription(request.description());
        claim.setStatus(ClaimStatus.MORE_INFO_PROVIDED);
        Claim saved = claimRepository.save(claim);
        log.info("Claim {} received additional information and is returning to the staff queue", claimNumber);
        staffClaimQueueService.makeAvailableAfterMoreInformation(saved.getId());
        return toResponse(saved);
    }

    public List<ClaimResponse> getClaimsByClaimantMemberNumber(String claimantMemberNumber) {
        Claimant claimant = claimantRepository.findByClaimantMemberNumber(claimantMemberNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claimant not found: " + claimantMemberNumber));

        return claimRepository.findByClaimantId(claimant.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getClaimNumber(),
                claim.getClaimant().getFirstName() + " " + claim.getClaimant().getLastName(),
                claim.getClaimant().getEmail(),
                claim.getClaimant().getPhone(),
                claim.getClaimant().getClaimantMemberNumber(),
                claim.getPolicyNumber(),
                claim.getClaimType(),
                claim.getStatus(),
                claim.getDescription(),
                claim.getCreatedAt(),
                claim.getUpdatedAt());
    }

    private String generateClaimNumber() {
        return "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}