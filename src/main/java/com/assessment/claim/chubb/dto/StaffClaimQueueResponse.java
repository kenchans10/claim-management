package com.assessment.claim.chubb.dto;

import com.assessment.claim.chubb.common.enums.ClaimStatus;
import com.assessment.claim.chubb.common.enums.ClaimType;
import com.assessment.claim.chubb.common.enums.QueueStatus;

import java.time.LocalDateTime;

public record StaffClaimQueueResponse(
        String claimNumber,
        String claimantMemberNumber,
        ClaimType claimType,
        ClaimStatus claimStatus,
        QueueStatus queueStatus,
        String staffNumber,
        LocalDateTime queuedAt,
        LocalDateTime pickedUpAt
) {
}