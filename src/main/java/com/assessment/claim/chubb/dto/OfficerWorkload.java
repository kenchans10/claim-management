package com.assessment.claim.chubb.dto;

import java.util.List;

public record OfficerWorkload(
        String staffNumber,
        int assignedClaims,
        int underAssessment,
        List<String> assignedClaimNumbers
) {
}