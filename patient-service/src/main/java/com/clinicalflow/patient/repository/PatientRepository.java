package com.clinicalflow.patient.repository;

import com.clinicalflow.patient.model.Patient;
import com.clinicalflow.patient.model.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByMrn(String mrn);

    List<Patient> findByStatus(PatientStatus status);

    List<Patient> findByLastNameIgnoreCaseContaining(String lastName);

    boolean existsByMrn(String mrn);
}
