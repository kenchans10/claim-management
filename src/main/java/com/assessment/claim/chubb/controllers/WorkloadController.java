package com.assessment.claim.chubb.controllers;

import com.assessment.claim.chubb.common.api.ApiResponse;
import com.assessment.claim.chubb.dto.WorkloadSummaryResponse;
import com.assessment.claim.chubb.services.WorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
/** Management endpoints for operational workload and liability reporting. */
public class WorkloadController {

    private final WorkloadService workloadService;

    @GetMapping("/management/claims")
    /** Returns claim status counts and staff assignment details. */
    public ResponseEntity<ApiResponse<WorkloadSummaryResponse>> getWorkloadSummary() {
        return ResponseEntity.ok(ApiResponse.success(workloadService.getWorkloadSummary()));
    }

}