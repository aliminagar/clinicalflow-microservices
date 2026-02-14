package com.clinicalflow.lab.event;

import java.time.Instant;
import java.util.UUID;

public class LabResultEvent {

    private UUID eventId;
    private String eventType;
    private UUID labResultId;
    private UUID patientId;
    private String testName;
    private Double value;
    private String unit;
    private String flag;
    private boolean critical;
    private Instant timestamp;

    public LabResultEvent() {}

    public static LabResultEvent of(String eventType, UUID labResultId, UUID patientId,
                                     String testName, Double value, String unit,
                                     String flag, boolean critical) {
        LabResultEvent event = new LabResultEvent();
        event.eventId = UUID.randomUUID();
        event.eventType = eventType;
        event.labResultId = labResultId;
        event.patientId = patientId;
        event.testName = testName;
        event.value = value;
        event.unit = unit;
        event.flag = flag;
        event.critical = critical;
        event.timestamp = Instant.now();
        return event;
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public UUID getLabResultId() { return labResultId; }
    public void setLabResultId(UUID labResultId) { this.labResultId = labResultId; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "LabResultEvent{type=" + eventType + ", test=" + testName +
               ", value=" + value + " " + unit + ", flag=" + flag +
               ", critical=" + critical + "}";
    }
}
