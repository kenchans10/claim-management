package com.assessment.claim.chubb.repository;

import com.assessment.claim.chubb.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
/** Persistence operations for staff records and business-number lookups. */
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByStaffNumber(String staffNumber);
}