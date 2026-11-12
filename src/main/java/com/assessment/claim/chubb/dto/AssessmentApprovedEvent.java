package com.assessment.claim.chubb.dto;

import java.io.Serializable;
import java.util.UUID;

public record AssessmentApprovedEvent(
        UUID assessmentId,
        UUID claimId,
        String claimNumber,
        String staffNumber,
        Double estimatedAmount,
        Double settledAmount
) implements Serializable {
}