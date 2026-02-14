package com.clinicalflow.lab.controller;

import com.clinicalflow.lab.model.LabResult;
import com.clinicalflow.lab.service.LabResultService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lab-results")
public class LabResultController {

    private final LabResultService service;

    public LabResultController(LabResultService service) {
        this.service = service;
    }

    @GetMapping("/patient/{patientId}")
    public List<LabResult> findByPatient(
            @PathVariable UUID patientId,
            @RequestParam(required = false) String testName) {
        if (testName != null) return service.findByPatientAndTest(patientId, testName);
        return service.findByPatient(patientId);
    }

    @GetMapping("/critical")
    public List<LabResult> findCritical() {
        return service.findCriticalResults();
    }

    @GetMapping("/{id}")
    public LabResult findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabResult submit(@Valid @RequestBody LabResult result) {
        return service.submitResult(result);
    }

    @PostMapping("/{id}/verify")
    public LabResult verify(@PathVariable UUID id) {
        return service.verifyResult(id);
    }
}
