package com.assessment.claim.chubb.dto;

import java.math.BigDecimal;

public record LiabilityExposureResponse(
        BigDecimal totalRequestedAmount,
        BigDecimal totalApprovedAmount,
        BigDecimal outstandingLiabilityExposure
) {
}