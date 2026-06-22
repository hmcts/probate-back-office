package uk.gov.hmcts.probate;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DmnDecisionTable {

    WA_TASK_COMPLETION_PROBATE(
            "wa-task-completion-probate-grantofrepresentation",
            "dmn/wa-task-completion-probate-grantofrepresentation.dmn"),
    WA_TASK_TYPES_PROBATE("wa-task-types-probate-grantofrepresentation",
            "dmn/wa-task-types-probate-grantofrepresentation.dmn"),
    WA_TASK_CANCELLATION_PROBATE(
            "wa-task-cancellation-probate-grantofrepresentation",
            "dmn/wa-task-cancellation-probate-grantofrepresentation.dmn"),
    WA_TASK_CONFIGURATION_PROBATE(
            "wa-task-configuration-probate-grantofrepresentation",
            "dmn/wa-task-configuration-probate-grantofrepresentation.dmn"),
    WA_TASK_INITIATION_PROBATE(
            "wa-task-initiation-probate-grantofrepresentation",
            "dmn/wa-task-initiation-probate-grantofrepresentation.dmn"),
    WA_TASK_PERMISSIONS_PROBATE(
            "wa-task-permissions-probate-grantofrepresentation",
            "dmn/wa-task-permissions-probate-grantofrepresentation.dmn");

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
