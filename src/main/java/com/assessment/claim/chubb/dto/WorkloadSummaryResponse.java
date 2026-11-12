package com.assessment.claim.chubb.dto;

import java.math.BigDecimal;
import java.util.List;

public record WorkloadSummaryResponse(
        int totalClaims,
        BigDecimal liabilityExposure,
        int assignedClaims,
        int underAssessment,
        int unassignedClaims,
        int outstandingClaims,
        List<OfficerWorkload> officerWorkloads
) {
}