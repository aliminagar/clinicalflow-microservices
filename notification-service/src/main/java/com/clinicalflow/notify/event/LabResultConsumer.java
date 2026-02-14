package com.clinicalflow.notify.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LabResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(LabResultConsumer.class);

    /**
     * Consumes lab result events from Kafka.
     *
     * In clinical practice, critical lab values (panic values) require immediate
     * notification to the ordering clinician — typically within 30 minutes per
     * CAP/CLIA regulations. This consumer simulates that alert workflow.
     */
    @KafkaListener(topics = "lab.results", groupId = "notification-service")
    public void handleLabResult(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        String testName = (String) event.get("testName");
        Object valueObj = event.get("value");
        String unit = (String) event.get("unit");
        String flag = (String) event.get("flag");
        Object criticalObj = event.get("critical");
        String patientId = String.valueOf(event.get("patientId"));

        boolean isCritical = criticalObj instanceof Boolean b && b;

        if (isCritical) {
            log.error("!!!! CRITICAL VALUE ALERT !!!!");
            log.error("Patient: {} | Test: {} | Value: {} {} | Flag: {}",
                    patientId, testName, valueObj, unit, flag);
            log.error("ACTION REQUIRED: Notify ordering clinician within 30 minutes (CAP/CLIA)");
            log.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } else {
            log.info("Lab result received: {} = {} {} [{}] for patient {}",
                    testName, valueObj, unit, flag, patientId);
        }
    }
}
