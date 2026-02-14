package com.clinicalflow.lab.service;

import com.clinicalflow.lab.event.LabResultEvent;
import com.clinicalflow.lab.event.LabResultEventPublisher;
import com.clinicalflow.lab.model.LabResult;
import com.clinicalflow.lab.model.ResultFlag;
import com.clinicalflow.lab.model.ResultStatus;
import com.clinicalflow.lab.repository.LabResultRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class LabResultService {

    private static final Logger log = LoggerFactory.getLogger(LabResultService.class);

    private final LabResultRepository repository;
    private final LabResultEventPublisher eventPublisher;

    public LabResultService(LabResultRepository repository, LabResultEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public List<LabResult> findByPatient(UUID patientId) {
        return repository.findByPatientIdOrderByCollectedAtDesc(patientId);
    }

    public List<LabResult> findByPatientAndTest(UUID patientId, String testName) {
        return repository.findByPatientIdAndTestNameOrderByCollectedAtDesc(patientId, testName);
    }

    public List<LabResult> findCriticalResults() {
        return repository.findByFlag(ResultFlag.CRITICAL);
    }

    public LabResult findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new LabResultNotFoundException("Lab result not found: " + id));
    }

    @Transactional
    public LabResult submitResult(LabResult result) {
        result.setStatus(ResultStatus.REPORTED);
        result.setReportedAt(Instant.now());
        result.evaluateFlag();

        LabResult saved = repository.save(result);

        String eventType = saved.isCritical() ? "CRITICAL_RESULT" : "RESULT_REPORTED";

        if (saved.isCritical()) {
            log.warn("CRITICAL VALUE DETECTED: {} = {} {} for patient {}",
                    saved.getTestName(), saved.getValue(), saved.getUnit(), saved.getPatientId());
        }

        eventPublisher.publish(LabResultEvent.of(
                eventType,
                saved.getId(),
                saved.getPatientId(),
                saved.getTestName(),
                saved.getValue(),
                saved.getUnit(),
                saved.getFlag().name(),
                saved.isCritical()
        ));

        return saved;
    }

    @Transactional
    public LabResult verifyResult(UUID id) {
        LabResult result = findById(id);
        result.setStatus(ResultStatus.VERIFIED);
        return repository.save(result);
    }

    public static class LabResultNotFoundException extends RuntimeException {
        public LabResultNotFoundException(String message) { super(message); }
    }
}
