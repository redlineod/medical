package com.redline.medical.controllers;

import com.redline.medical.dto.VisitCreateRequest;
import com.redline.medical.dto.VisitCreateResponse;
import com.redline.medical.services.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/visits")
@Slf4j
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<VisitCreateResponse> createVisit(@Valid @RequestBody VisitCreateRequest request) {

        try {
            VisitCreateResponse response = visitService.createVisit(request);

            if (response == null || response.getId() == null) {
                throw new RuntimeException("Failed to create visit - invalid response");
            }

            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (Exception e) {
            log.error("Failed to create visit for patient {} with doctor {}: {}", 
                     request.getPatientId(), request.getDoctorId(), e.getMessage());
            throw e;
        }
    }
}
