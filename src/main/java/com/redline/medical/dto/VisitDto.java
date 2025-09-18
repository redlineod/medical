package com.redline.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitDto {
    private String start;
    private String end;
    private DoctorDto doctor;
}
