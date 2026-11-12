package com.assessment.claim.chubb.services;

import com.assessment.claim.chubb.common.enums.AssessmentResult;
import com.assessment.claim.chubb.common.enums.ClaimStatus;
import com.assessment.claim.chubb.common.exceptions.BadRequestException;
import com.assessment.claim.chubb.common.exceptions.ResourceNotFoundException;
import com.assessment.claim.chubb.dto.*;
import com.assessment.claim.chubb.entities.Assessment;
import com.assessment.claim.chubb.entities.Claim;
import com.assessment.claim.chubb.entities.Staff;
import com.assessment.claim.chubb.repository.AssessmentRepository;
import com.assessment.claim.chubb.repository.ClaimRepository;
import com.assessment.claim.chubb.repository.StaffRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
/**
 * Coordinates assessment persistence, claim decisions, and downstream events.
 */
public class AssessmentService {

//    private static final Logger log = LoggerFactory.getLogger(AssessmentService.class);

    private final AssessmentRepository assessmentRepository;
    private final ClaimRepository claimRepository;
    private final StaffRepository staffRepository;
    private final NotificationService notificationService;

    @Transactional
    /** Saves an assessment and routes its result to the next workflow queue. */
    public AssessmentResponse createAssessment(CreateAssessmentRequest request) {
        Claim claim = claimRepository.findByClaimNumber(request.claimNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + request.claimNumber()));

        Staff staff = staffRepository.findByStaffNumber(request.staffNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + request.staffNumber()));

        Assessment assessment = new Assessment();
        assessment.setClaim(claim);
        assessment.setStaff(staff);
        assessment.setAssessmentType(request.assessmentType());
        assessment.setDescription(request.description());
        assessment.setDetails(request.details());
        assessment.setEstimatedAmount(request.estimatedAmount());
        assessment.setResult(request.result());

        claim.setStatus(toClaimStatus(request.result()));
        claimRepository.save(claim);

        Assessment saved = assessmentRepository.save(assessment);
        log.info("Assessment {} completed for claim {} with result {}", saved.getId(), claim.getClaimNumber(),
                request.result());
        if (request.result() == AssessmentResult.APPROVED) {
            notificationService.publishAssessmentApproved(new AssessmentApprovedEvent(
                    saved.getId(),
                    claim.getId(),
                    claim.getClaimNumber(),
                    staff.getStaffNumber(),
                    saved.getEstimatedAmount(),
                    saved.getSettledAmount()));
        } else if (request.result() == AssessmentResult.REJECTED
                || request.result() == AssessmentResult.MORE_INFO_REQUIRED) {
            notificationService.publishAssessmentRejected(new AssessmentRejectedEvent(
                    saved.getId(),
                    claim.getId(),
                    claim.getClaimNumber(),
                    claim.getClaimant().getFirstName(),
                    claim.getClaimant().getEmail(),
                    request.result(),
                    saved.getDescription()));
        }
        return toResponse(saved);
    }

    public ClaimResponse startAssessment(String claimNumber, String staffNumber) {
        log.info("Starting assessment for claim {} by staff {}", claimNumber, staffNumber);
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));

        staffRepository.findByStaffNumber(staffNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));

        if (claim.getStatus() != ClaimStatus.ASSIGNED
                && claim.getStatus() != ClaimStatus.MORE_INFO_REQUESTED) {
            throw new BadRequestException("Assessment cannot be started in status: " + claim.getStatus());
        }

        claim.setStatus(ClaimStatus.ASSESSMENT_IN_PROGRESS);
        Claim saved = claimRepository.save(claim);
        return toClaimResponse(saved);
    }

    public AssessmentResponse getAssessment(UUID assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + assessmentId));
    }

    public List<AssessmentResponse> getAssessmentsByClaim(String claimNumber) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));

        return assessmentRepository.findByClaimId(claim.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AssessmentResponse> getAssessmentsByStaff(String staffNumber) {
        Staff staff = staffRepository.findByStaffNumber(staffNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));

        return assessmentRepository.findByStaffId(staff.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AssessmentResponse updateSettledAmount(UUID assessmentId, Double settledAmount) {
        log.info("Updating settled amount for assessment {}", assessmentId);
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + assessmentId));

        assessment.setSettledAmount(settledAmount);
        Assessment saved = assessmentRepository.save(assessment);
        return toResponse(saved);
    }

    private AssessmentResponse toResponse(Assessment assessment) {
        return new AssessmentResponse(
                assessment.getId(),
                assessment.getClaim().getClaimNumber(),
                assessment.getStaff().getStaffNumber(),
                assessment.getAssessmentType(),
                assessment.getDescription(),
                assessment.getDetails(),
                assessment.getEstimatedAmount(),
                assessment.getSettledAmount(),
                assessment.getResult(),
                assessment.getCreatedAt(),
                assessment.getUpdatedAt());
    }

    private ClaimStatus toClaimStatus(AssessmentResult result) {
        return switch (result) {
            case APPROVED -> ClaimStatus.APPROVED;
            case REJECTED -> ClaimStatus.REJECTED;
            case MORE_INFO_REQUIRED -> ClaimStatus.MORE_INFO_REQUESTED;
        };
    }

    private ClaimResponse toClaimResponse(Claim claim) {
        return new ClaimResponse(
                claim.getClaimNumber(),
                claim.getClaimant().getClaimantMemberNumber(),
                claim.getClaimType(),
                claim.getStatus(),
                claim.getDescription(),
                claim.getCreatedAt(),
                claim.getUpdatedAt());
    }
}