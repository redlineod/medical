package com.redline.medical.services.impl;

import com.redline.medical.dto.PatientDto;
import com.redline.medical.dto.PatientsGetResponse;
import com.redline.medical.mappers.PatientQueryResultMapper;
import com.redline.medical.repositories.PatientRepository;
import com.redline.medical.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientQueryResultMapper queryResultMapper;

    @Override
    public PatientsGetResponse getPatients(int page, int size, String search, List<Long> doctorIds) {

        int offset = page * size;

        List<Object[]> queryResults = patientRepository.findPatientsWithLastVisits(
                search, doctorIds, size, offset);

        List<PatientDto> patients = queryResultMapper.convertToPatientDtos(queryResults);

        long totalCount = patientRepository.countPatients(search, doctorIds);

        return PatientsGetResponse.builder()
                .data(patients)
                .count((int) totalCount)
                .build();
    }

}
