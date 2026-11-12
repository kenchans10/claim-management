package com.assessment.claim.chubb.dto;

import java.io.Serializable;
import java.util.UUID;

public record ClaimReadyForStaffEvent(UUID claimId, String claimNumber) implements Serializable {
}