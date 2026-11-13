package com.assessment.claim.chubb.controllers;

import com.assessment.claim.chubb.common.api.ApiResponse;
import com.assessment.claim.chubb.dto.CreateClaimantRequest;
import com.assessment.claim.chubb.entities.Claimant;
import com.assessment.claim.chubb.services.ClaimantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
/** REST endpoints for claimant creation and lookup. */
public class ClaimantController {

    private final ClaimantService claimantService;

    @PostMapping("/claimants")
    /** Registers a claimant profile. */
    public ResponseEntity<ApiResponse<Claimant>> createClaimant(@Valid @RequestBody CreateClaimantRequest request) {
        Claimant claimant = claimantService.createClaimant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(claimant, "Claimant created successfully"));
    }

    @GetMapping("/claimants/{id}")
    /** Retrieves a claimant by internal identifier. */
    public ResponseEntity<ApiResponse<Claimant>> getClaimant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(claimantService.getClaimant(id)));
    }

    @GetMapping("/claimants")
    /** Retrieves a claimant by internal identifier. */
    public ResponseEntity<ApiResponse<List<Claimant>>> getListClaimant() {
        return ResponseEntity.ok(ApiResponse.success(claimantService.getListClaimant()));
    }
}