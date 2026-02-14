package com.clinicalflow.patient.controller;

import com.clinicalflow.patient.model.Patient;
import com.clinicalflow.patient.model.PatientStatus;
import com.clinicalflow.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @GetMapping
    public List<Patient> findAll(
            @RequestParam(required = false) PatientStatus status,
            @RequestParam(required = false) String lastName) {
        if (status != null) return service.findByStatus(status);
        if (lastName != null) return service.searchByLastName(lastName);
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Patient findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/mrn/{mrn}")
    public Patient findByMrn(@PathVariable String mrn) {
        return service.findByMrn(mrn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient register(@Valid @RequestBody Patient patient) {
        return service.register(patient);
    }

    @PatchMapping("/{id}")
    public Patient update(@PathVariable UUID id, @RequestBody Patient patch) {
        return service.update(id, patch);
    }

    @PostMapping("/{id}/admit")
    public Patient admit(@PathVariable UUID id) {
        return service.admit(id);
    }

    @PostMapping("/{id}/discharge")
    public Patient discharge(@PathVariable UUID id) {
        return service.discharge(id);
    }

    @PostMapping("/{id}/transfer")
    public Patient transfer(@PathVariable UUID id) {
        return service.transfer(id);
    }
}
