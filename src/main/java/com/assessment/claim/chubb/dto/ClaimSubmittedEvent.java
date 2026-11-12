package com.assessment.claim.chubb.dto;

import java.io.Serializable;
import java.util.UUID;

public record ClaimSubmittedEvent(UUID claimId, UUID claimantId, String claimNumber) implements Serializable {
}