package com.assessment.claim.chubb.services;

import com.assessment.claim.chubb.common.exceptions.ResourceNotFoundException;
import com.assessment.claim.chubb.dto.StaffResponse;
import com.assessment.claim.chubb.entities.Staff;
import com.assessment.claim.chubb.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
/** Provides staff lookup operations for public API boundaries. */
public class StaffService {

    private final StaffRepository staffRepository;

    public List<StaffResponse> getStaff() {
        return staffRepository.findAll().stream().map(this::toResponse).toList();
    }

    public StaffResponse getStaff(String staffNumber) {
        return staffRepository.findByStaffNumber(staffNumber)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));
    }

    private StaffResponse toResponse(Staff staff) {
        return new StaffResponse(
                staff.getStaffNumber(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getRole(),
                staff.getCreatedAt());
    }
}