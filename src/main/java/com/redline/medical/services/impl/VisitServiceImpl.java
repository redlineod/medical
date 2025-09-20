package com.redline.medical.services.impl;

import com.redline.medical.dto.VisitCreateRequest;
import com.redline.medical.dto.VisitCreateResponse;
import com.redline.medical.entities.Doctor;
import com.redline.medical.entities.Patient;
import com.redline.medical.entities.Visit;
import com.redline.medical.repositories.DoctorRepository;
import com.redline.medical.repositories.PatientRepository;
import com.redline.medical.repositories.VisitRepository;
import com.redline.medical.services.VisitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public VisitCreateResponse createVisit(VisitCreateRequest request) {
        validateCreateVisitRequest(request);
        
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        
        ZoneId doctorZone = ZoneId.of(doctor.getTimezone());
        ZonedDateTime startLocal = request.getStart().atZone(doctorZone);
        ZonedDateTime endLocal = request.getEnd().atZone(doctorZone);
        Instant startUtc = startLocal.toInstant();
        Instant endUtc = endLocal.toInstant();

        if (endUtc.isBefore(startUtc) || endUtc.equals(startUtc)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        if (visitRepository
                .existsByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        request.getDoctorId(), endUtc, startUtc)) {
            throw new IllegalArgumentException("Doctor already has a conflicting visit at this time");
        }

        Visit visit = Visit.builder()
                .doctor(doctor)
                .patient(patient)
                .startDateTime(startUtc)
                .endDateTime(endUtc)
                .build();
        
        Visit savedVisit = visitRepository.save(visit);

        return VisitCreateResponse.builder()
                .id(savedVisit.getId())
                .start(savedVisit.getStartDateTime().atZone(doctorZone).toLocalDateTime())
                .end(savedVisit.getEndDateTime().atZone(doctorZone).toLocalDateTime())
                .build();
    }
    
    private void validateCreateVisitRequest(VisitCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Visit request cannot be null");
        }
        
        if (request.getDoctorId() == null || request.getDoctorId() <= 0) {
            throw new IllegalArgumentException("Valid doctor ID is required");
        }
        
        if (request.getPatientId() == null || request.getPatientId() <= 0) {
            throw new IllegalArgumentException("Valid patient ID is required");
        }
        
        if (request.getStart() == null) {
            throw new IllegalArgumentException("Start time is required");
        }
        
        if (request.getEnd() == null) {
            throw new IllegalArgumentException("End time is required");
        }

        LocalDateTime now = LocalDateTime.now();
        if (request.getStart().isBefore(now)) {
            throw new IllegalArgumentException("Visit cannot be scheduled in the past");
        }
        
        if (request.getEnd().isBefore(request.getStart().plusMinutes(15))) {
            throw new IllegalArgumentException("Visit must be at least 15 minutes long");
        }
    }
}
