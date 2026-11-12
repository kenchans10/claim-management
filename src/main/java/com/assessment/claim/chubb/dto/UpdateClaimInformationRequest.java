package com.assessment.claim.chubb.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateClaimInformationRequest(
        @NotBlank String description
) {
}