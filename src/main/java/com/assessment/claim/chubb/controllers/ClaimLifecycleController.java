package com.assessment.claim.chubb.controllers;

import com.assessment.claim.chubb.common.api.ApiResponse;
import com.assessment.claim.chubb.dto.ClaimResponse;
import com.assessment.claim.chubb.services.ClaimLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
/** REST endpoints for explicit claim lifecycle transitions. */
public class ClaimLifecycleController {

    private final ClaimLifecycleService claimLifecycleService;

    @PostMapping("/claims/{claimNumber}/assign")
    /** Assigns a claim to a staff member using public business identifiers. */
    public ResponseEntity<ApiResponse<ClaimResponse>> assignClaim(
            @PathVariable String claimNumber, @RequestParam String staffNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                claimLifecycleService.assignClaim(claimNumber, staffNumber), "Claim assigned successfully"));
    }
}