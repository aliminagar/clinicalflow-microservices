package com.clinicalflow.patient.service;

import com.clinicalflow.patient.event.PatientEvent;
import com.clinicalflow.patient.event.PatientEventPublisher;
import com.clinicalflow.patient.model.Patient;
import com.clinicalflow.patient.model.PatientStatus;
import com.clinicalflow.patient.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository repository;
    private final PatientEventPublisher eventPublisher;

    public PatientService(PatientRepository repository, PatientEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public List<Patient> findAll() {
        return repository.findAll();
    }

    public Patient findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found: " + id));
    }

    public Patient findByMrn(String mrn) {
        return repository.findByMrn(mrn)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with MRN: " + mrn));
    }

    public List<Patient> findByStatus(PatientStatus status) {
        return repository.findByStatus(status);
    }

    public List<Patient> searchByLastName(String lastName) {
        return repository.findByLastNameIgnoreCaseContaining(lastName);
    }

    @Transactional
    public Patient register(Patient patient) {
        if (repository.existsByMrn(patient.getMrn())) {
            throw new DuplicateMrnException("MRN already exists: " + patient.getMrn());
        }

        patient.setStatus(PatientStatus.REGISTERED);
        Patient saved = repository.save(patient);

        eventPublisher.publish(PatientEvent.of(
                "PATIENT_REGISTERED",
                saved.getId(),
                saved.getMrn(),
                saved.getFirstName() + " " + saved.getLastName(),
                null,
                PatientStatus.REGISTERED.name()
        ));

        return saved;
    }

    @Transactional
    public Patient admit(UUID id) {
        return transitionStatus(id, PatientStatus.ADMITTED, "PATIENT_ADMITTED");
    }

    @Transactional
    public Patient discharge(UUID id) {
        return transitionStatus(id, PatientStatus.DISCHARGED, "PATIENT_DISCHARGED");
    }

    @Transactional
    public Patient transfer(UUID id) {
        return transitionStatus(id, PatientStatus.TRANSFERRED, "PATIENT_TRANSFERRED");
    }

    @Transactional
    public Patient update(UUID id, Patient patch) {
        Patient existing = findById(id);
        if (patch.getFirstName() != null) existing.setFirstName(patch.getFirstName());
        if (patch.getLastName() != null) existing.setLastName(patch.getLastName());
        if (patch.getDateOfBirth() != null) existing.setDateOfBirth(patch.getDateOfBirth());
        return repository.save(existing);
    }

    private Patient transitionStatus(UUID id, PatientStatus newStatus, String eventType) {
        Patient patient = findById(id);
        String previousStatus = patient.getStatus().name();
        patient.setStatus(newStatus);
        Patient saved = repository.save(patient);

        eventPublisher.publish(PatientEvent.of(
                eventType,
                saved.getId(),
                saved.getMrn(),
                saved.getFirstName() + " " + saved.getLastName(),
                previousStatus,
                newStatus.name()
        ));

        return saved;
    }

    public static class PatientNotFoundException extends RuntimeException {
        public PatientNotFoundException(String message) { super(message); }
    }

    public static class DuplicateMrnException extends RuntimeException {
        public DuplicateMrnException(String message) { super(message); }
    }
}
