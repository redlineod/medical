package com.redline.medical.services.impl;

import com.redline.medical.dto.PatientsGetResponse;
import com.redline.medical.mappers.PatientQueryResultMapper;
import com.redline.medical.repositories.PatientRepository;
import com.redline.medical.services.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientQueryResultMapper queryResultMapper;

    @Override
    public PatientsGetResponse getPatients(int page, int size, String search, List<Long> doctorIds) {
        try {
            log.debug("Fetching patients - page: {}, size: {}, search: '{}', doctorIds: {}",
                    page, size, search, doctorIds);

            int offset = page * size;

            List<Object[]> queryResults = patientRepository.findPatientsWithLastVisitsAndCount(
                    search, doctorIds, size, offset);

            PatientsGetResponse response = queryResultMapper.convertToPatientGetResponse(queryResults);

            log.debug("Successfully fetched {} patients", response.getCount());
            return response;

        } catch (DataAccessException ex) {
            log.error("Database error occurred while fetching patients - page: {}, size: {}, search: '{}', doctorIds: {}",
                    page, size, search, doctorIds, ex);
            throw new RuntimeException("Failed to retrieve patients from database", ex);

        } catch (Exception ex) {
            log.error("Unexpected error occurred while processing patients data - page: {}, size: {}, search: '{}', doctorIds: {}",
                    page, size, search, doctorIds, ex);
            throw new RuntimeException("Failed to process patients data", ex);
        }

    }

}
