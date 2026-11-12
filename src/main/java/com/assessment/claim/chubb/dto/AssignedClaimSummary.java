package com.assessment.claim.chubb.dto;

import com.assessment.claim.chubb.common.enums.ClaimStatus;

public record AssignedClaimSummary(
        String claimNumber,
        ClaimStatus status
) {
}