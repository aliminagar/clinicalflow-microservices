package com.clinicalflow.patient.event;

import java.time.Instant;
import java.util.UUID;

public class PatientEvent {

    private UUID eventId;
    private String eventType;
    private UUID patientId;
    private String mrn;
    private String patientName;
    private String previousStatus;
    private String newStatus;
    private Instant timestamp;

    public PatientEvent() {}

    public static PatientEvent of(String eventType, UUID patientId, String mrn,
                                   String patientName, String previousStatus, String newStatus) {
        PatientEvent event = new PatientEvent();
        event.eventId = UUID.randomUUID();
        event.eventType = eventType;
        event.patientId = patientId;
        event.mrn = mrn;
        event.patientName = patientName;
        event.previousStatus = previousStatus;
        event.newStatus = newStatus;
        event.timestamp = Instant.now();
        return event;
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }

    public String getMrn() { return mrn; }
    public void setMrn(String mrn) { this.mrn = mrn; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "PatientEvent{type=" + eventType + ", patientId=" + patientId +
               ", mrn=" + mrn + ", status=" + previousStatus + " -> " + newStatus + "}";
    }
}
