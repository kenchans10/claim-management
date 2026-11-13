package com.assessment.claim.chubb.dto;

import com.assessment.claim.chubb.common.enums.ClaimStatus;
import com.assessment.claim.chubb.common.enums.ClaimType;

import java.time.LocalDateTime;

public record ClaimResponse(
        String claimNumber,
        String fullName,
        String email,
        String phone,
        String claimantMemberNumber,
        String policyNumber,
        ClaimType claimType,
        ClaimStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}