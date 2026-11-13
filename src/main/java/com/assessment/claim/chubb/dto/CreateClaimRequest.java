package com.assessment.claim.chubb.dto;

import com.assessment.claim.chubb.common.enums.ClaimType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateClaimRequest(
        @NotBlank String claimantMemberNumber,
        @NotNull ClaimType claimType,
        @NotBlank String policyNumber,
        @NotBlank String description
) {
}