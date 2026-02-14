package com.clinicalflow.lab.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LabResultEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LabResultEventPublisher.class);

    private final KafkaTemplate<String, LabResultEvent> kafkaTemplate;
    private final String topic;

    public LabResultEventPublisher(
            KafkaTemplate<String, LabResultEvent> kafkaTemplate,
            @Value("${app.kafka.topic.lab-results}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(LabResultEvent event) {
        log.info("Publishing lab event: {}", event);
        kafkaTemplate.send(topic, event.getPatientId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish lab event: {}", event, ex);
                    } else {
                        log.info("Lab event published to partition {} offset {}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
