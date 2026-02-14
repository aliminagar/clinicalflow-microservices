package com.clinicalflow.notify.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PatientEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PatientEventConsumer.class);

    @KafkaListener(topics = "patient.events", groupId = "notification-service")
    public void handlePatientEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        String patientName = (String) event.get("patientName");
        String mrn = (String) event.get("mrn");
        String newStatus = (String) event.get("newStatus");

        switch (eventType) {
            case "PATIENT_ADMITTED" -> log.info(
                    "ALERT: Patient {} (MRN: {}) has been ADMITTED. Initiating admission workflow.",
                    patientName, mrn);
            case "PATIENT_DISCHARGED" -> log.info(
                    "ALERT: Patient {} (MRN: {}) has been DISCHARGED. Generating discharge summary.",
                    patientName, mrn);
            case "PATIENT_TRANSFERRED" -> log.info(
                    "ALERT: Patient {} (MRN: {}) TRANSFER initiated. Notifying receiving unit.",
                    patientName, mrn);
            case "PATIENT_REGISTERED" -> log.info(
                    "INFO: New patient registered: {} (MRN: {})",
                    patientName, mrn);
            default -> log.info("Received patient event: {} for {} (MRN: {})",
                    eventType, patientName, mrn);
        }
    }
}
