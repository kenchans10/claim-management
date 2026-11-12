package com.assessment.claim.chubb.dto;

import com.assessment.claim.chubb.common.enums.AssessmentResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentResponse(
        UUID id,
        String claimNumber,
        String staffNumber,
        String assessmentType,
        String description,
        String details,
        Double estimatedAmount,
        Double settledAmount,
        AssessmentResult result,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}