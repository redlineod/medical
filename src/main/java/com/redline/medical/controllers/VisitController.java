package com.redline.medical.controllers;

import com.redline.medical.dto.VisitCreateRequest;
import com.redline.medical.dto.VisitCreateResponse;
import com.redline.medical.services.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/visits")
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<VisitCreateResponse> createVisit(@Valid @RequestBody VisitCreateRequest request) {

        return new ResponseEntity<>(visitService.createVisit(request), HttpStatus.CREATED);
    }
}
