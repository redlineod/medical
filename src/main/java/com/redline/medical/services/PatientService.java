package com.redline.medical.services;

import com.redline.medical.dto.PatientsGetResponse;

import java.util.List;

public interface PatientService {
    PatientsGetResponse getPatients (int page, int size, String search, List<Long> doctorIds);
}
