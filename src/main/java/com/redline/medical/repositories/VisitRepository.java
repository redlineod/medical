package com.redline.medical.repositories;

import com.redline.medical.entities.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    boolean existsByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long doctorId, Instant end, Instant start);
}
