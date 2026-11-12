package com.assessment.claim.chubb.listener;

import com.assessment.claim.chubb.dto.AssessmentRejectedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
/**
 * Listens for rejected assessments and represents the customer email
 * notification step.
 *
 * For the current time constraints, the actual SMTP integration is mocked by
 * logging
 * the notification details instead of sending an email.
 */
public class AssessmentRejectedEmailListener {
    // JavaMailSender will replace the logging mock when SMTP integration is added.

    public AssessmentRejectedEmailListener() {
    }

    @JmsListener(destination = "assessment-rejected-queue")
    /**
     * Consumes a rejection event and performs the current mocked email
     * notification.
     */
    public void handle(AssessmentRejectedEvent event) {
        // Mock email delivery until the mail provider and credentials are configured.
        log.info("Sent {} email for claim {} to {}", event.result(), event.claimNumber(), event.claimantEmail());
    }
}