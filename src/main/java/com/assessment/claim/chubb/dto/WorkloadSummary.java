package com.assessment.claim.chubb.dto;

import com.assessment.claim.chubb.common.enums.ClaimStatus;

import java.util.List;

public record WorkloadSummary(
        ClaimStatus status,
        long count,
        List<String> claimNumbers
) {
}