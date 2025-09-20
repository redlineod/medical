package com.redline.medical.repositories;

import com.redline.medical.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query(value = """
             WITH patient_filter AS (
                 SELECT DISTINCT p.id
                 FROM patients p
                 LEFT JOIN visits v ON p.id = v.patient_id
                 WHERE (:search IS NULL
                        OR LOWER(p.first_name) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR LOWER(p.last_name) LIKE LOWER(CONCAT('%', :search, '%')))
                   AND (:doctorIds IS NULL OR v.doctor_id IN (:doctorIds))
             ),
             total_patients AS (
                SELECT COUNT(*) AS total_count FROM patient_filter
             ),
             patient_page AS (
                 SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn
                 FROM patient_filter
                 ORDER BY id
                 LIMIT :size OFFSET :offset
             ),
             ranked_visits AS (
                 SELECT
                     v.patient_id,
                     v.doctor_id,
                     v.start_datetime,
                     v.end_datetime,
                     ROW_NUMBER() OVER (PARTITION BY v.patient_id, v.doctor_id ORDER BY v.start_datetime DESC) as visit_rn
                 FROM visits v
                 INNER JOIN patient_page pp ON v.patient_id = pp.id
                 WHERE (:doctorIds IS NULL OR v.doctor_id IN (:doctorIds))
             ),
             doctor_stats AS (
                     SELECT
                         doctor_id,
                         COUNT(DISTINCT patient_id) as total_patients
                     FROM visits v
                     WHERE v.doctor_id IN (SELECT DISTINCT rv.doctor_id FROM ranked_visits rv)
                     GROUP BY doctor_id
             )
             SELECT
                 p.id AS patient_id,
                 p.first_name AS patient_first_name,
                 p.last_name AS patient_last_name,
                 rv.start_datetime,
                 rv.end_datetime,
                 d.first_name AS doctor_first_name,
                 d.last_name AS doctor_last_name,
                 ds.total_patients,
                 tp.total_count
             FROM patient_page pp
             INNER JOIN patients p ON p.id = pp.id
             LEFT JOIN ranked_visits rv ON p.id = rv.patient_id AND rv.visit_rn = 1
             LEFT JOIN doctors d ON d.id = rv.doctor_id
             LEFT JOIN doctor_stats ds ON ds.doctor_id = d.id
             CROSS JOIN total_patients tp
             ORDER BY p.id
            """, nativeQuery = true)
    List<Object[]> findPatientsWithLastVisitsAndCount(
            @Param("search") String search,
            @Param("doctorIds") List<Long> doctorIds,
            @Param("size") int size,
            @Param("offset") int offset
    );

}
