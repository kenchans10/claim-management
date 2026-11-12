package com.assessment.claim.chubb.services;

import com.assessment.claim.chubb.common.enums.AssessmentResult;
import com.assessment.claim.chubb.common.enums.ClaimStatus;
import com.assessment.claim.chubb.common.enums.QueueStatus;
import com.assessment.claim.chubb.dto.OfficerWorkload;
import com.assessment.claim.chubb.dto.WorkloadSummaryResponse;
import com.assessment.claim.chubb.entities.Assessment;
import com.assessment.claim.chubb.entities.Claim;
import com.assessment.claim.chubb.entities.Staff;
import com.assessment.claim.chubb.entities.StaffClaimQueue;
import com.assessment.claim.chubb.repository.AssessmentRepository;
import com.assessment.claim.chubb.repository.ClaimRepository;
import com.assessment.claim.chubb.repository.StaffClaimQueueRepository;
import com.assessment.claim.chubb.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
/**
 * Builds operational workload summaries from claims, assessments, and queues.
 */
public class WorkloadService {

    private final ClaimRepository claimRepository;
    private final AssessmentRepository assessmentRepository;
    private final StaffRepository staffRepository;
    private final StaffClaimQueueRepository staffClaimQueueRepository;

    public WorkloadSummaryResponse getWorkloadSummary() {
        log.debug("Building combined workload and liability summary");
        List<Claim> claims = claimRepository.findAll();
        List<StaffClaimQueue> pickedUpClaims = staffClaimQueueRepository
                .findByStatusOrderByQueuedAtAsc(QueueStatus.PICKED_UP);
        List<StaffClaimQueue> availableClaims = staffClaimQueueRepository
                .findByStatusOrderByQueuedAtAsc(QueueStatus.AVAILABLE);

        int assignedClaims = pickedUpClaims.size();
        int underAssessment = (int) claims.stream()
                .filter(claim -> claim.getStatus() == ClaimStatus.ASSESSMENT_IN_PROGRESS)
                .count();
        int unassignedClaims = availableClaims.size();
        int outstandingClaims = (int) claims.stream()
                .filter(claim -> claim.getStatus() != ClaimStatus.APPROVED
                        && claim.getStatus() != ClaimStatus.REJECTED
                        && claim.getStatus() != ClaimStatus.CLOSED)
                .count();

        return new WorkloadSummaryResponse(
                claims.size(),
                calculateLiabilityExposure(),
                assignedClaims,
                underAssessment,
                unassignedClaims,
                outstandingClaims,
                staffRepository.findAll().stream()
                        .map(staff -> toOfficerWorkload(staff, pickedUpClaims))
                        .toList());
    }

    private OfficerWorkload toOfficerWorkload(Staff staff, List<StaffClaimQueue> pickedUpClaims) {
        List<Claim> officerClaims = pickedUpClaims.stream()
                .filter(queue -> queue.getStaff().getId().equals(staff.getId()))
                .map(StaffClaimQueue::getClaim)
                .toList();
        int underAssessment = (int) officerClaims.stream()
                .filter(claim -> claim.getStatus() == ClaimStatus.ASSESSMENT_IN_PROGRESS)
                .count();
        return new OfficerWorkload(
                staff.getStaffNumber(),
                officerClaims.size(),
                underAssessment,
                officerClaims.stream()
                        .map(claim -> claim.getClaimNumber())
                        .toList());
    }

    private BigDecimal calculateLiabilityExposure() {
        List<Assessment> assessments = assessmentRepository.findAll();
        BigDecimal totalRequestedAmount = assessments.stream()
                .map(Assessment::getEstimatedAmount)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalApprovedAmount = assessments.stream()
                .filter(assessment -> assessment.getResult() == AssessmentResult.APPROVED)
                .map(Assessment::getEstimatedAmount)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalRequestedAmount.subtract(totalApprovedAmount);
    }
}