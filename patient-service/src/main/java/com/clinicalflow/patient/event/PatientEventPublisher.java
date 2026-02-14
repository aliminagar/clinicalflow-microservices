package com.clinicalflow.patient.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PatientEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PatientEventPublisher.class);

    private final KafkaTemplate<String, PatientEvent> kafkaTemplate;
    private final String topic;

    public PatientEventPublisher(
            KafkaTemplate<String, PatientEvent> kafkaTemplate,
            @Value("${app.kafka.topic.patient-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(PatientEvent event) {
        log.info("Publishing event: {}", event);
        kafkaTemplate.send(topic, event.getPatientId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event: {}", event, ex);
                    } else {
                        log.info("Event published successfully to partition {} offset {}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
