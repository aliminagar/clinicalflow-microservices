package com.clinicalflow.lab.controller;

import com.clinicalflow.lab.model.LabResult;
import com.clinicalflow.lab.service.LabResultService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller Layer
 * ----------------
 * Handles incoming HTTP requests for lab results.
 * Delegates business logic to the service layer.
 */
@RestController
@RequestMapping("/api/lab-results")
public class LabResultController {

    private final LabResultService service;

    // Constructor injection of service layer
    public LabResultController(LabResultService service) {
        this.service = service;
    }

    /**
     * GET /api/lab-results/patient/{patientId}
     * Fetch lab results for a patient.
     * Optionally filter by test name.
     */
    @GetMapping("/patient/{patientId}")
    public List<LabResult> findByPatient(
            @PathVariable UUID patientId,
            @RequestParam(required = false) String testName) {

        // If testName is provided → filter results
        if (testName != null)
            return service.findByPatientAndTest(patientId, testName);

        // Otherwise → return all results for patient
        return service.findByPatient(patientId);
    }

    /**
     * GET /api/lab-results/critical
     * Fetch all critical lab results (e.g., abnormal values).
     */
    @GetMapping("/critical")
    public List<LabResult> findCritical() {
        return service.findCriticalResults();
    }

    /**
     * GET /api/lab-results/{id}
     * Fetch a single lab result by its ID.
     */
    @GetMapping("/{id}")
    public LabResult findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    /**
     * POST /api/lab-results
     * Submit a new lab result.
     * @Valid ensures request body is validated.
     * Returns HTTP 201 (Created).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabResult submit(@Valid @RequestBody LabResult result) {
        return service.submitResult(result);
    }

    /**
     * POST /api/lab-results/{id}/verify
     * Mark a lab result as verified.
     */
    @PostMapping("/{id}/verify")
    public LabResult verify(@PathVariable UUID id) {
        return service.verifyResult(id);
    }
}
