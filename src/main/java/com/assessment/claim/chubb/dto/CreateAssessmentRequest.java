package com.assessment.claim.chubb.dto;

import com.assessment.claim.chubb.common.enums.AssessmentResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssessmentRequest(
        @NotBlank String claimNumber,
        @NotBlank String staffNumber,
        @NotBlank String assessmentType,
        @NotBlank String description,
        String details,
        @NotNull Double estimatedAmount,
        @NotNull AssessmentResult result
) {
}