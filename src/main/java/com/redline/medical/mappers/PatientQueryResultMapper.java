package com.redline.medical.mappers;

import com.redline.medical.dto.DoctorDto;
import com.redline.medical.dto.PatientDto;
import com.redline.medical.dto.VisitDto;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class PatientQueryResultMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public List<PatientDto> convertToPatientDtos(List<Object[]> queryResults) {
        Map<Long, PatientDto> patientMap = new LinkedHashMap<>();

        for (Object[] row : queryResults) {
            Long patientId = ((Number) row[0]).longValue();
            String firstName = (String) row[1];
            String lastName = (String) row[2];
            Timestamp startTimestamp = (Timestamp) row[3];
            Timestamp endTimestamp = (Timestamp) row[4];
            String doctorFirstName = (String) row[5];
            String doctorLastName = (String) row[6];
            Integer totalPatients = row[7] != null ? ((Number) row[7]).intValue() : 0;

            PatientDto patientDto = patientMap.computeIfAbsent(patientId, id ->
                    PatientDto.builder()
                            .firstName(firstName)
                            .lastName(lastName)
                            .lastVisits(new ArrayList<>())
                            .build()
            );

            if (startTimestamp != null && doctorFirstName != null) {
                String startFormatted = formatTimestamp(startTimestamp);
                String endFormatted = formatTimestamp(endTimestamp);

                DoctorDto doctorDto = DoctorDto.builder()
                        .firstName(doctorFirstName)
                        .lastName(doctorLastName)
                        .totalPatients(totalPatients)
                        .build();

                VisitDto visitDto = VisitDto.builder()
                        .start(startFormatted)
                        .end(endFormatted)
                        .doctor(doctorDto)
                        .build();

                patientDto.getLastVisits().add(visitDto);
            }
        }

        return new ArrayList<>(patientMap.values());
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return null;

        Instant instant = timestamp.toInstant();
        return instant.atZone(ZoneId.of("UTC"))
                .format(ISO_FORMATTER);
    }

}
