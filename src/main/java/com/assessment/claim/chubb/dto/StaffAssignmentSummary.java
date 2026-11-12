package com.assessment.claim.chubb.dto;

import java.util.List;

public record StaffAssignmentSummary(
        String staffNumber,
        List<AssignedClaimSummary> assignedClaims
) {
}