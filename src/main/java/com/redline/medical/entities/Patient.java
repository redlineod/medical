package com.redline.medical.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_patients_first_name", columnList = "first_name"),
        @Index(name = "idx_patients_last_name", columnList = "last_name")
})
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "patient")
    private List<Visit> visits;
}
