package uk.gov.hmcts.probate;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DmnDecisionTable {

    WA_TASK_ALLOWED_DAYS_WA_WACASETYPE("wa-task-allowed-days-wa-wacasetype",
            "wa-task-allowed-days-wa-wacasetype.dmn"),
    WA_TASK_COMPLETION_ST_CIC_CRIMINALINJURIESCOMPENSATION(
            "wa-task-completion-st_cic-criminalinjuriescompensation",
            "dmn/wa-task-completion-st_cic-criminalinjuriescompensation.dmn"),
    WA_TASK_TYPES_ST_CIC_CRIMINALINJURIESCOMPENSATION("wa-task-types-st_cic-criminalinjuriescompensation",
            "dmn/wa-task-types-st_cic-criminalinjuriescompensation.dmn"),
    WA_TASK_CANCELLATION_ST_CIC_CRIMINALINJURIESCOMPENSATION(
            "wa-task-cancellation-st_cic-criminalinjuriescompensation",
            "dmn/wa-task-cancellation-st_cic-criminalinjuriescompensation.dmn"),
    WA_TASK_CONFIGURATION_ST_CIC_CRIMINALINJURIESCOMPENSATION(
            "wa-task-configuration-st_cic-criminalinjuriescompensation",
            "dmn/wa-task-configuration-st_cic-criminalinjuriescompensation.dmn"),
    WA_TASK_INITIATION_ST_CIC_CRIMINALINJURIESCOMPENSATION(
            "wa-task-initiation-st_cic-criminalinjuriescompensation",
            "dmn/wa-task-initiation-st_cic-criminalinjuriescompensation.dmn"),
    WA_TASK_PERMISSIONS_ST_CIC_CRIMINALINJURIESCOMPENSATION(
            "wa-task-permissions-st_cic-criminalinjuriescompensation",
            "dmn/wa-task-permissions-st_cic-criminalinjuriescompensation.dmn");

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
