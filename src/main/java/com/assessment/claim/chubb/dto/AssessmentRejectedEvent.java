package com.assessment.claim.chubb.dto;

import com.assessment.claim.chubb.common.enums.AssessmentResult;

import java.io.Serializable;
import java.util.UUID;

public record AssessmentRejectedEvent(
        UUID assessmentId,
        UUID claimId,
        String claimNumber,
        String claimantFirstName,
        String claimantEmail,
        AssessmentResult result,
        String assessmentDescription
) implements Serializable {
}