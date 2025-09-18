package com.redline.medical.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "visits", indexes = {
        @Index(name = "idx_visits_doctor_id", columnList = "doctor_id"),
        @Index(name = "idx_visits_patient_doctor", columnList = "patient_id, doctor_id"),
        @Index(name = "idx_visits_doctor_times", columnList = "doctor_id, start_datetime, end_datetime")
})
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_datetime")
    private Instant startDateTime;

    @Column(name = "end_datetime")
    private Instant endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;


}
