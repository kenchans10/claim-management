package com.assessment.claim.chubb.services;

import com.assessment.claim.chubb.dto.AssessmentApprovedEvent;
import com.assessment.claim.chubb.dto.AssessmentRejectedEvent;
import com.assessment.claim.chubb.dto.ClaimReadyForStaffEvent;
import com.assessment.claim.chubb.dto.ClaimSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
/**
 * Stores domain events in the transactional outbox for asynchronous delivery.
 */
public class NotificationService {

    private static final String CLAIM_EVENTS_QUEUE = "claim-events";
    private static final String STAFF_CLAIM_QUEUE = "staff-claim-queue";
    private static final String FINANCE_TEAM_QUEUE = "finance-team-queue";
    private static final String ASSESSMENT_REJECTED_QUEUE = "assessment-rejected-queue";

    private final OutboxEventService outboxEventService;

    /** Stores a legacy claim-submitted event for downstream consumers. */
    public void publishClaimSubmitted(ClaimSubmittedEvent event) {
        outboxEventService.enqueue(CLAIM_EVENTS_QUEUE, event);
        log.info("Stored claim submitted event for claim {} in the outbox", event.claimNumber());
    }

    /** Stores a claim intake event for staff queue processing. */
    public void publishClaimReadyForStaff(ClaimReadyForStaffEvent event) {
        outboxEventService.enqueue(STAFF_CLAIM_QUEUE, event);
        log.info("Stored claim ready for staff event for claim {} in the outbox", event.claimNumber());
    }

    /** Stores an approved assessment event for finance processing. */
    public void publishAssessmentApproved(AssessmentApprovedEvent event) {
        outboxEventService.enqueue(FINANCE_TEAM_QUEUE, event);
        log.info("Stored approved assessment for claim {} in the outbox", event.claimNumber());
    }

    /** Stores a rejected assessment event for claimant notification. */
    public void publishAssessmentRejected(AssessmentRejectedEvent event) {
        outboxEventService.enqueue(ASSESSMENT_REJECTED_QUEUE, event);
        log.info("Stored rejected assessment for claim {} in the outbox", event.claimNumber());
    }
}