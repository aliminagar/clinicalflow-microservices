package com.clinicalflow.lab.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lab_results")
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Patient ID is required")
    @Column(nullable = false)
    private UUID patientId;

    @NotBlank(message = "Test name is required")
    @Column(nullable = false)
    private String testName;

    @NotNull(message = "Test value is required")
    @Column(nullable = false)
    private Double value;

    @NotBlank(message = "Unit is required")
    @Column(nullable = false)
    private String unit;

    private Double referenceMin;
    private Double referenceMax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultFlag flag = ResultFlag.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status = ResultStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant collectedAt;

    private Instant reportedAt;

    @PrePersist
    void onCreate() {
        if (collectedAt == null) collectedAt = Instant.now();
        evaluateFlag();
    }

    /**
     * Clinical flag evaluation based on reference ranges.
     * Critical values trigger immediate notification workflows.
     *
     * In real clinical practice, critical values (also called "panic values")
     * require immediate clinician notification — typically within 30 minutes.
     */
    public void evaluateFlag() {
        if (referenceMin == null || referenceMax == null) {
            flag = ResultFlag.NORMAL;
            return;
        }

        double range = referenceMax - referenceMin;
        double criticalLowThreshold = referenceMin - (range * 0.5);
        double criticalHighThreshold = referenceMax + (range * 0.5);

        if (value < criticalLowThreshold || value > criticalHighThreshold) {
            flag = ResultFlag.CRITICAL;
        } else if (value < referenceMin) {
            flag = ResultFlag.LOW;
        } else if (value > referenceMax) {
            flag = ResultFlag.HIGH;
        } else {
            flag = ResultFlag.NORMAL;
        }
    }

    public boolean isCritical() {
        return flag == ResultFlag.CRITICAL;
    }

    public boolean isAbnormal() {
        return flag != ResultFlag.NORMAL;
    }

    // --- Getters and Setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getReferenceMin() { return referenceMin; }
    public void setReferenceMin(Double referenceMin) { this.referenceMin = referenceMin; }

    public Double getReferenceMax() { return referenceMax; }
    public void setReferenceMax(Double referenceMax) { this.referenceMax = referenceMax; }

    public ResultFlag getFlag() { return flag; }
    public void setFlag(ResultFlag flag) { this.flag = flag; }

    public ResultStatus getStatus() { return status; }
    public void setStatus(ResultStatus status) { this.status = status; }

    public Instant getCollectedAt() { return collectedAt; }
    public void setCollectedAt(Instant collectedAt) { this.collectedAt = collectedAt; }

    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }
}
