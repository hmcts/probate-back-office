package uk.gov.hmcts.probate;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DmnDecisionTable {

    WA_TASK_COMPLETION_PROBATE(
            "wa-task-completion-probate",
            "dmn/wa-task-completion-probate.dmn"),
    WA_TASK_TYPES_PROBATE("wa-task-types-probate",
            "dmn/wa-task-types-probate.dmn"),
    WA_TASK_CANCELLATION_PROBATE(
            "wa-task-cancellation-probate",
            "dmn/wa-task-cancellation-probate.dmn"),
    WA_TASK_CONFIGURATION_PROBATE(
            "wa-task-configuration-probate",
            "dmn/wa-task-configuration-probate.dmn"),
    WA_TASK_INITIATION_PROBATE(
            "wa-task-initiation-probate",
            "dmn/wa-task-initiation-probate.dmn"),
    WA_TASK_PERMISSIONS_PROBATE(
            "wa-task-permissions-probate",
            "dmn/wa-task-permissions-probate.dmn");

    @JsonValue
    private final String key;
    private final String fileName;

    DmnDecisionTable(String key, String fileName) {
        this.key = key;
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public String getFileName() {
        return fileName;
    }
}
