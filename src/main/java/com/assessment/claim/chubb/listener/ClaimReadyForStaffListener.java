package com.assessment.claim.chubb.listener;

import com.assessment.claim.chubb.dto.ClaimReadyForStaffEvent;
import com.assessment.claim.chubb.services.StaffClaimQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
/** Consumes claim intake events and creates durable staff queue entries. */
public class ClaimReadyForStaffListener {

    private final StaffClaimQueueService queueService;

    public ClaimReadyForStaffListener(StaffClaimQueueService queueService) {
        this.queueService = queueService;
    }

    @JmsListener(destination = "staff-claim-queue")
    /** Idempotently enqueues a newly submitted claim for staff pickup. */
    public void handle(ClaimReadyForStaffEvent event) {
        queueService.enqueue(event.claimId());
        log.info("Enqueued claim {} for staff pickup", event.claimNumber());
    }
}